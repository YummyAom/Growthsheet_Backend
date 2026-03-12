package com.growthsheet.product_service.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Set;
import java.util.stream.Collectors;

import com.growthsheet.product_service.dto.PageResponse;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import com.growthsheet.product_service.dto.CategoryDTO;
import com.growthsheet.product_service.dto.SellerDTO;
import com.growthsheet.product_service.dto.UniversityDTO;
import com.growthsheet.product_service.dto.request.CreateSheetRequest;
import com.growthsheet.product_service.dto.response.DownloadResponse;
import com.growthsheet.product_service.dto.response.ProductResponseDTO;
import com.growthsheet.product_service.dto.response.SheetCardResponse;
import com.growthsheet.product_service.dto.response.SheetResponse;
import com.growthsheet.product_service.entity.Category;
import com.growthsheet.product_service.entity.Hashtag;
import com.growthsheet.product_service.entity.Sheet;
import com.growthsheet.product_service.entity.SheetReview;
import com.growthsheet.product_service.entity.SheetStatus;
import com.growthsheet.product_service.mapper.SheetCardMapper;
import com.growthsheet.product_service.repository.CategoryRepository;
import com.growthsheet.product_service.repository.HashtagRepository;
import com.growthsheet.product_service.repository.ReviewRepository;
import com.growthsheet.product_service.repository.SheetRepository;
import com.growthsheet.product_service.repository.UserRepository;
import com.growthsheet.product_service.config.client.OrderClient;
import com.growthsheet.product_service.dto.client.OrderResponse;

import jakarta.transaction.Transactional;

@Service
public class SheetService {

        private final SheetRepository sheetRepo;
        private final CategoryRepository categoryRepo;
        private final HashtagService hashtagService;
        private final SheetImageService sheetImageService;
        private final UniversityService universityService;
        private final UserRepository userRepo;
        private final ReviewRepository reviewRepo;
        private final SheetAssembler sheetAssembler;
        private final SheetCardMapper sheetCardMapper;
        private final HashtagRepository hashtagRepository;
        private final OrderClient orderClient;

        public SheetService(
                        SheetRepository sheetRepo,
                        CategoryRepository categoryRepo,
                        HashtagService hashtagService,
                        SheetImageService sheetImageService,
                        UniversityService universityService,
                        UserRepository userRepo,
                        ReviewRepository reviewRepo,
                        SheetAssembler sheetAssembler,
                        SheetCardMapper sheetCardMapper,
                        OrderClient orderClient,
                        HashtagRepository hashtagRepository) {
                this.sheetRepo = sheetRepo;
                this.categoryRepo = categoryRepo;
                this.hashtagService = hashtagService;
                this.sheetImageService = sheetImageService;
                this.universityService = universityService;
                this.userRepo = userRepo;
                this.reviewRepo = reviewRepo;
                this.sheetAssembler = sheetAssembler;
                this.sheetCardMapper = sheetCardMapper;
                this.orderClient = orderClient;
                this.hashtagRepository = hashtagRepository;
        }

        public PageResponse<SheetCardResponse> getPurchasedSheets(UUID userId, Pageable pageable) {

                // 1. ดึงข้อมูล Order ที่ชำระเงินแล้วทั้งหมดมาจาก Order Service
                PageResponse<OrderResponse> orderPage = orderClient.getPaidOrders(userId, pageable);

                if (orderPage == null || orderPage.getContent() == null || orderPage.getContent().isEmpty()) {
                        return new PageResponse<>(
                                        List.of(),
                                        pageable.getPageNumber(),
                                        pageable.getPageSize(),
                                        0,
                                        0,
                                        true);
                }

                // 2. 🌟 กรองเอาเฉพาะ Sheet ID ของ Item ที่ "ยังไม่ถูก Refund" เท่านั้น 🌟
                Set<UUID> validSheetIds = orderPage.getContent().stream()
                                .filter(order -> order.getItems() != null)
                                .flatMap(order -> order.getItems().stream())
                                // ✅ เพิ่มบรรทัดนี้: เช็คว่าต้องไม่เป็น True (เป็น null หรือ false ถือว่ายังไม่
                                // Refund)
                                .filter(item -> item.getIsRefunded() == null || !item.getIsRefunded())
                                .map(OrderResponse.Item::getSheetId)
                                .collect(Collectors.toSet());

                if (validSheetIds.isEmpty()) {
                        return new PageResponse<>(
                                        List.of(),
                                        orderPage.getPage(),
                                        orderPage.getSize(),
                                        orderPage.getTotalElements(),
                                        orderPage.getTotalPages(),
                                        orderPage.isLast());
                }

                // 3. ไปดึงข้อมูลรายละเอียดของ Sheet จากฐานข้อมูล Product Service
                List<Sheet> sheets = sheetRepo.findAllById(validSheetIds);

                // 4. แมปข้อมูลส่งกลับไปให้ Frontend
                List<SheetCardResponse> content = sheets.stream()
                                .map(this::toSheetCardResponse)
                                .toList();

                return new PageResponse<>(
                                content,
                                orderPage.getPage(),
                                orderPage.getSize(),
                                orderPage.getTotalElements(),
                                orderPage.getTotalPages(),
                                orderPage.isLast());
        }

        @Transactional
        public List<String> getAllTags() {
                return hashtagRepository.findAllTagNames();
        }

        @Transactional
        public SheetResponse createSheet(
                        CreateSheetRequest req,
                        UUID sellerId,
                        Map<String, Object> pdf,
                        List<String> images) {

                Category category = categoryRepo.findById(req.categoryId())
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Category not found"));

                Sheet sheet = new Sheet();
                sheet.setUniversity(universityService.findOrNull(req.universityId()));
                sheet.setTitle(req.title());
                sheet.setDescription(req.description());
                sheet.setPrice(req.price());

                sheet.setFileUrl((String) pdf.get("url"));
                sheet.setPageCount((Integer) pdf.get("pageCount"));

                sheet.setCourseCode(req.courseCode());
                sheet.setCourseName(req.courseName());
                sheet.setStudyYear(req.studyYear());
                sheet.setAcademicYear(req.academicYear());

                sheet.setCategory(category);
                sheet.setSellerId(sellerId);
                sheet.setStatus(SheetStatus.PENDING);
                sheet.setIsPublished(false);

                sheet.setHashtags(
                                hashtagService.resolveHashtags(req.hashtags()));

                sheetRepo.save(sheet);

                sheetImageService.attachPreviewImages(sheet, images);

                return SheetResponse.from(sheet);
        }

        private Sort getSort(String sort) {
                return switch (sort) {
                        case "price_asc" -> Sort.by("price").ascending();
                        case "price_desc" -> Sort.by("price").descending();
                        case "rating" -> Sort.by("averageRating").descending();
                        case "popular" -> Sort.by("likeCount").descending();
                        case "latest" -> Sort.by("createdAt").descending();
                        default -> Sort.by("createdAt").descending();
                };
        }

        private SheetCardResponse toSheetCardResponse(Sheet sheet) {

                System.out.println(">>> mapping sheetId=" + sheet.getId()
                                + " status=" + sheet.getStatus()
                                + " isPublished=" + sheet.getIsPublished());

                SellerDTO seller = userRepo.findById(sheet.getSellerId())
                                .map(u -> new SellerDTO(u.getId(), u.getName()))
                                .orElse(null);

                System.out.println(">>> seller=" + seller);

                return sheetCardMapper.toResponse(sheet, seller);
        }

        public String getSheetFileUrl(UUID sheetId, UUID userId) {

                Sheet sheet = sheetRepo.findById(sheetId)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Sheet not found"));

                if (sheet.getStatus() != SheetStatus.APPROVED) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Sheet is not available");
                }

                boolean purchased = orderClient.hasPurchased(userId, sheetId);

                if (!purchased) {
                        throw new ResponseStatusException(
                                        HttpStatus.FORBIDDEN,
                                        "You have not purchased this sheet");
                }

                if (sheet.getFileUrl() == null) {
                        throw new ResponseStatusException(
                                        HttpStatus.INTERNAL_SERVER_ERROR,
                                        "File not available");
                }

                return sheet.getFileUrl();
        }
        // ใน SheetService.java

        public boolean isSheetPurchased(UUID sheetId, UUID userId) {
                try {
                        // ใช้ orderClient ที่คุณมีอยู่แล้วในการเช็คกับ Order Service โดยตรง
                        // ซึ่งคุณใช้ตัวนี้อยู่แล้วใน getSheetFileUrl และ getDownloadInfo
                        return orderClient.hasPurchased(userId, sheetId);
                } catch (Exception e) {
                        // กรณี Order Service ล่ม ให้ return false ไว้ก่อนเพื่อความปลอดภัย
                        return false;
                }
        }

        public DownloadResponse getDownloadInfo(UUID sheetId, UUID userId) {

                Sheet sheet = sheetRepo.findById(sheetId)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Sheet not found"));

                if (sheet.getStatus() != SheetStatus.APPROVED) {
                        throw new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Sheet is not available");
                }

                boolean purchased = orderClient.hasPurchased(userId, sheetId);

                if (!purchased) {
                        throw new ResponseStatusException(
                                        HttpStatus.FORBIDDEN,
                                        "You have not purchased this sheet");
                }

                if (sheet.getFileUrl() == null) {
                        throw new ResponseStatusException(
                                        HttpStatus.INTERNAL_SERVER_ERROR,
                                        "File not available");
                }

                return new DownloadResponse(
                                sheet.getFileUrl(),
                                sheet.getTitle());
        }

        public DownloadResponse getDownloadInfoAdmin(UUID sheetId) {

                Sheet sheet = sheetRepo.findById(sheetId)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Sheet not found"));

                if (sheet.getFileUrl() == null) {
                        throw new ResponseStatusException(
                                        HttpStatus.INTERNAL_SERVER_ERROR,
                                        "File not available");
                }

                if (sheet.getFileUrl() == null) {
                        throw new ResponseStatusException(
                                        HttpStatus.INTERNAL_SERVER_ERROR,
                                        "File not available");
                }

                return new DownloadResponse(
                                sheet.getFileUrl(),
                                sheet.getTitle());
        }

        public Page<SheetCardResponse> getSheets(
                        int page,
                        int size,
                        String sort,
                        Boolean isPublished,
                        List<String> tags) {

                Pageable pageable = PageRequest.of(page, size, getSort(sort));

                Page<Sheet> sheets;

                boolean hasTags = tags != null && !tags.isEmpty();

                if (hasTags) {

                        if (Boolean.TRUE.equals(isPublished)) {

                                sheets = sheetRepo.findPublishedSheetsByTags(
                                                tags,
                                                List.of(SheetStatus.APPROVED),
                                                pageable);

                        } else if (Boolean.FALSE.equals(isPublished)) {

                                sheets = sheetRepo.findUnpublishedSheetsByTags(
                                                tags,
                                                List.of(SheetStatus.PENDING, SheetStatus.REJECTED),
                                                pageable);

                        } else {

                                sheets = sheetRepo.findSheetsByTags(tags, pageable);
                        }

                } else {

                        if (Boolean.TRUE.equals(isPublished)) {

                                sheets = sheetRepo.findByStatusInAndIsPublished(
                                                List.of(SheetStatus.APPROVED),
                                                true,
                                                pageable);

                        } else if (Boolean.FALSE.equals(isPublished)) {

                                sheets = sheetRepo.findByStatusInAndIsPublished(
                                                List.of(SheetStatus.PENDING, SheetStatus.REJECTED),
                                                false,
                                                pageable);

                        } else {

                                sheets = sheetRepo.findAll(pageable);
                        }
                }

                return sheets.map(this::toSheetCardResponse);
        }

        public SheetCardResponse getSheetById(UUID sheetId) {

                Sheet sheet = sheetRepo.findById(sheetId)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Sheet not found"));

                return sheetAssembler.assemble(sheet);
        }

        public Page<SheetCardResponse> findSheetPageByUserId(
                        UUID sellerId,
                        int page,
                        int size,
                        Boolean isPublished) {

                Pageable pageable = PageRequest.of(
                                page,
                                size,
                                Sort.by(Sort.Direction.DESC, "createdAt"));

                Page<Sheet> sheets;

                if (isPublished == null) {
                        // ทั้งหมด
                        sheets = sheetRepo.findAllBySellerId(sellerId, pageable);
                } else {
                        // กรองตามสถานะ
                        sheets = sheetRepo.findAllBySellerIdAndIsPublished(
                                        sellerId, isPublished, pageable);
                }

                return sheets.map(sheetAssembler::assemble);
        }

        public ProductResponseDTO getSheet(UUID sheetId) {

                Sheet sheet = sheetRepo.findById(sheetId)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Sheet not found"));

                SellerDTO seller = userRepo.findById(sheet.getSellerId())
                                .map(u -> new SellerDTO(u.getId(), u.getName()))
                                .orElse(null);

                return new ProductResponseDTO(
                                sheet.getId(),
                                sheet.getTitle(),
                                sheet.getDescription(),
                                sheet.getPrice(),

                                // image
                                (sheet.getPreviewImages() == null || sheet.getPreviewImages().isEmpty())
                                                ? null
                                                : sheet.getPreviewImages().get(0).getImageUrl(),

                                sheet.getFileUrl(),

                                // university
                                sheet.getUniversity() == null
                                                ? null
                                                : new UniversityDTO(
                                                                sheet.getUniversity().getId(),
                                                                sheet.getUniversity().getNameEn()),

                                // category
                                sheet.getCategory() == null
                                                ? null
                                                : new CategoryDTO(
                                                                sheet.getCategory().getId(),
                                                                sheet.getCategory().getName()),

                                // tags
                                sheet.getHashtags() == null
                                                ? List.of()
                                                : sheet.getHashtags().stream()
                                                                .map(Hashtag::getName)
                                                                .toList(),

                                // ratingCount
                                sheet.getReviewCount() == null ? 0 : sheet.getReviewCount(),

                                // ratingAverage
                                sheet.getAverageRating() == null
                                                ? 0.0
                                                : sheet.getAverageRating().doubleValue(),

                                // seller
                                seller,

                                // isPublished
                                sheet.getIsPublished(),

                                // pageCount
                                sheet.getPageCount(),

                                // createdAt
                                sheet.getCreatedAt(),

                                // updatedAt
                                sheet.getUpdatedAt());
        }

        @Transactional
        public void createReview(
                        UUID sheetId,
                        UUID userId,
                        int rating,
                        String comment) {
                if (reviewRepo.existsBySheetIdAndUserId(sheetId, userId)) {
                        throw new IllegalStateException("You already reviewed this sheet");
                }

                SheetReview review = new SheetReview();
                review.setSheetId(sheetId);
                review.setUserId(userId);
                review.setRating(rating);
                review.setComment(comment);

                reviewRepo.save(review);
        }

        @Transactional
        public void approveSheet(UUID sheetId) {
                Sheet sheet = sheetRepo.findById(sheetId)
                                .orElseThrow(() -> new RuntimeException("Sheet not found"));

                sheet.setStatus(SheetStatus.APPROVED);
                sheet.setIsPublished(true);
                sheetRepo.save(sheet);
        }

        @Transactional
        public void rejectSheet(UUID sheetId, String adminNote) {
                Sheet sheet = sheetRepo.findById(sheetId)
                                .orElseThrow(() -> new RuntimeException("Sheet not found"));

                sheet.setStatus(SheetStatus.REJECTED);
                sheet.setIsPublished(false);
                sheet.setAdminNote(adminNote);
        }

        /**
         * ดูประวัติการขอ publish sheet ของ seller
         */
        public Page<SheetCardResponse> getSheetPublicationHistory(UUID sellerId,
                        String status,
                        Boolean isDeleted,
                        Boolean suspended,
                        int page,
                        int size) {

                Pageable pageable = PageRequest.of(
                                page,
                                size,
                                Sort.by(Sort.Direction.DESC, "createdAt"));

                Page<Sheet> sheets;

                // --- ลำดับการกรองที่ถูกต้อง ---
                if (Boolean.TRUE.equals(isDeleted)) {
                        // 1. ถ้าส่งมาว่าอยากดูที่ถูกลบ ให้ดึงที่ถูกลบเท่านั้น (Priority สูงสุด)
                        sheets = sheetRepo.findAllBySellerIdAndIsDeletedTrue(sellerId, pageable);

                } else if (Boolean.TRUE.equals(suspended)) {
                        // 2. ถ้าอยากดูที่ถูกระงับ ต้องเป็น APPROVED + NOT PUBLISHED และต้อง
                        // "ยังไม่ถูกลบ"
                        // (Repository ของเราถูกแก้ให้เช็ค isDeleted = false แล้ว)
                        sheets = sheetRepo.findAllBySellerIdAndStatusAndIsPublishedFalse(
                                        sellerId,
                                        SheetStatus.APPROVED,
                                        pageable);

                } else if (status != null && !status.isBlank()) {
                        // 3. ถ้ากรองตาม Status (PENDING/REJECTED) ต้องดึงเฉพาะที่ยังไม่ถูกลบ
                        SheetStatus sheetStatus = SheetStatus.valueOf(status.toUpperCase());
                        sheets = sheetRepo.findAllBySellerIdAndStatusAndIsDeletedFalse(
                                        sellerId,
                                        sheetStatus,
                                        pageable);
                } else {
                        // 4. กรณี "ทั้งหมด" ดึงเฉพาะที่ยังไม่ถูกลบ
                        sheets = sheetRepo.findAllBySellerIdAndIsDeletedFalse(sellerId, pageable);
                }

                // --- ส่วนดึงยอดขายจาก Order Service ---
                List<UUID> sheetIds = sheets.getContent().stream()
                                .map(Sheet::getId)
                                .toList();

                java.util.Map<UUID, Long> salesCountMap = new java.util.HashMap<>();
                if (!sheetIds.isEmpty()) {
                        try {
                                salesCountMap = orderClient.getSalesCountsBySheetIds(sheetIds);
                        } catch (Exception e) {
                                System.err.println("ไม่สามารถดึงยอดขายได้: " + e.getMessage());
                        }
                }

                final java.util.Map<UUID, Long> finalSalesCountMap = salesCountMap;

                return sheets.map(sheet -> {
                        SheetCardResponse baseResponse = sheetAssembler.assemble(sheet);
                        Long count = finalSalesCountMap.getOrDefault(sheet.getId(), 0L);
                        return baseResponse.withSalesCount(count.intValue());
                });
        }

        /**
         * ดูชีทของ Seller ที่ถูกระบบระงับ (คือ status=APPROVED แต่ isPublished=false)
         */
        public Page<SheetCardResponse> getSuspendedSheets(UUID sellerId, int page, int size) {
                Pageable pageable = PageRequest.of(
                                page,
                                size,
                                Sort.by(Sort.Direction.DESC, "updatedAt"));

                return sheetRepo.findAllBySellerIdAndStatusAndIsPublishedFalse(
                                sellerId,
                                SheetStatus.APPROVED,
                                pageable)
                                .map(sheetAssembler::assemble);
        }

        /**
         * ทำ Soft Delete โดยการปรับสถานะ isPublished เป็น false
         */
        @Transactional
        public void softDeleteSheet(UUID sheetId, UUID sellerId) {

                Sheet sheet = sheetRepo.findById(sheetId)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "ไม่พบข้อมูลชีทที่ต้องการลบ"));

                if (!sheet.getSellerId().equals(sellerId)) {
                        throw new ResponseStatusException(
                                        HttpStatus.FORBIDDEN, "คุณไม่มีสิทธิ์ลบชีทนี้");
                }

                sheet.setIsDeleted(true);
                sheet.setIsPublished(false);
                sheet.setDeletedAt(OffsetDateTime.now());

                sheetRepo.save(sheet);
        }
}
