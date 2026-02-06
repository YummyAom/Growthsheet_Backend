package com.growthsheet.product_service.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import com.growthsheet.product_service.dto.response.ProductResponseDTO;
import com.growthsheet.product_service.dto.response.SheetResponse;
import com.growthsheet.product_service.entity.Category;
import com.growthsheet.product_service.entity.Hashtag;
import com.growthsheet.product_service.entity.Sheet;
import com.growthsheet.product_service.entity.SheetStatus;
import com.growthsheet.product_service.repository.CategoryRepository;
import com.growthsheet.product_service.repository.SheetRepository;
import com.growthsheet.product_service.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class SheetService {

        private final SheetRepository sheetRepo;
        private final CategoryRepository categoryRepo;
        private final HashtagService hashtagService;
        private final SheetImageService sheetImageService;
        private final UniversityService universityService;
        private final UserRepository userRepo;

        public SheetService(
                        SheetRepository sheetRepo,
                        CategoryRepository categoryRepo,
                        HashtagService hashtagService,
                        SheetImageService sheetImageService,
                        UniversityService universityService,
                        UserRepository userRepo) {
                this.sheetRepo = sheetRepo;
                this.categoryRepo = categoryRepo;
                this.hashtagService = hashtagService;
                this.sheetImageService = sheetImageService;
                this.universityService = universityService;
                this.userRepo = userRepo;
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

                sheet.setFileUrl( (String) pdf.get("url"));
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

        public Page<ProductResponseDTO> getSheets(int page, int size) {

                Pageable pageable = PageRequest.of(
                                page,
                                size,
                                Sort.by(Sort.Direction.DESC, "createdAt"));

                return sheetRepo.findByStatus(SheetStatus.APPROVED, pageable)
                                .map(sheet -> {

                                        SellerDTO seller = userRepo.findById(sheet.getSellerId())
                                                        .map(u -> new SellerDTO(u.getId(), u.getName()))
                                                        .orElse(null);

                                        return new ProductResponseDTO(
                                                        sheet.getId(),
                                                        sheet.getTitle(),
                                                        sheet.getDescription(),
                                                        sheet.getPrice(),

                                                        // image
                                                        (sheet.getPreviewImages() == null
                                                                        || sheet.getPreviewImages().isEmpty())
                                                                                        ? null
                                                                                        : sheet.getPreviewImages()
                                                                                                        .get(0)
                                                                                                        .getImageUrl(),

                                                        sheet.getFileUrl(),

                                                        // university
                                                        sheet.getUniversity() == null
                                                                        ? null
                                                                        : new UniversityDTO(
                                                                                        sheet.getUniversity().getId(),
                                                                                        sheet.getUniversity()
                                                                                                        .getNameEn()),

                                                        // category
                                                        sheet.getCategory() == null
                                                                        ? null
                                                                        : new CategoryDTO(
                                                                                        sheet.getCategory().getId(),
                                                                                        sheet.getCategory().getName()),

                                                        // tags
                                                        sheet.getHashtags()
                                                                        .stream()
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

                                });
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

}