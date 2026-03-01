package com.growthsheet.product_service.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Set;
import java.util.stream.Collectors;

import com.growthsheet.product_service.dto.PageResponse;
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
                        OrderClient orderClient) {
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
        }

        public PageResponse<SheetCardResponse> getPurchasedSheets(UUID userId, Pageable pageable) {

                PageResponse<OrderResponse> orderPage = orderClient.getPaidOrders(userId, pageable);

                Set<UUID> sheetIds = orderPage.getContent().stream()
                                .flatMap(order -> order.getItems().stream())
                                .map(OrderResponse.Item::getSheetId)
                                .collect(Collectors.toSet());

                List<Sheet> sheets = sheetRepo.findAllById(sheetIds);

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
                sheet.setStatus(SheetStatus.APPROVED);
                sheet.setIsPublished(true);

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

                SellerDTO seller = userRepo.findById(sheet.getSellerId())
                                .map(u -> new SellerDTO(u.getId(), u.getName()))
                                .orElse(null);

                return sheetCardMapper.toResponse(sheet, seller);
        }

        public String getSheetFileUrl(UUID sheetId, UUID userId) {

                Sheet sheet = sheetRepo.findById(sheetId)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Sheet not found"));

                if (!sheet.getIsPublished() || sheet.getStatus() != SheetStatus.APPROVED) {
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

        public DownloadResponse getDownloadInfo(UUID sheetId, UUID userId) {

                Sheet sheet = sheetRepo.findById(sheetId)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Sheet not found"));

                if (!sheet.getIsPublished() || sheet.getStatus() != SheetStatus.APPROVED) {
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

        public Page<SheetCardResponse> getSheets(
                        int page,
                        int size,
                        String sort,
                        Boolean isPublished // null = ทั้งหมด
        ) {
                Pageable pageable = PageRequest.of(page, size, getSort(sort));

                Page<Sheet> sheets;

                if (isPublished == null) {
                        // ดึงทั้งหมดทุกสถานะ (หรือเฉพาะที่ต้องการ เช่น APPROVED, PENDING, REJECTED)
                        sheets = sheetRepo.findAll(pageable);
                } else if (isPublished) {
                        // true = กรองเฉพาะ APPROVED
                        sheets = sheetRepo.findByStatusInAndIsPublished(
                                        List.of(SheetStatus.APPROVED),
                                        true,
                                        pageable);
                } else {
                        // false = กรอง PENDING และ REJECTED
                        sheets = sheetRepo.findByStatusInAndIsPublished(
                                        List.of(SheetStatus.PENDING, SheetStatus.REJECTED),
                                        false,
                                        pageable);
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
        }

        @Transactional
        public void rejectSheet(UUID sheetId) {
                Sheet sheet = sheetRepo.findById(sheetId)
                                .orElseThrow(() -> new RuntimeException("Sheet not found"));

                sheet.setStatus(SheetStatus.REJECTED);
                sheet.setIsPublished(false);
        }

        @Transactional
        public void createReview(UUID sheetId, UUID userId) {
        }
}
