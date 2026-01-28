package com.growthsheet.product_service.service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import com.growthsheet.product_service.dto.request.CreateSheetRequest;
import com.growthsheet.product_service.dto.response.SheetResponse;
import com.growthsheet.product_service.entity.Category;
import com.growthsheet.product_service.entity.Hashtag;
import com.growthsheet.product_service.entity.Sheet;
import com.growthsheet.product_service.entity.SheetStatus;
import com.growthsheet.product_service.repository.CategoryRepository;
import com.growthsheet.product_service.repository.SheetRepository;
import com.growthsheet.product_service.repository.UniversityRepository;

import jakarta.transaction.Transactional;

@Service
public class SheetService {

    private final SheetRepository sheetRepo;
    private final CategoryRepository categoryRepo;
    private final HashtagService hashtagService;
    private final SheetImageService sheetImageService;
    private final UniversityService universityService;

    public SheetService(
            SheetRepository sheetRepo,
            CategoryRepository categoryRepo,
            UniversityRepository universityRepo,
            HashtagService hashtagService,
            SheetImageService sheetImageService,
            UniversityService universityService) {

        this.sheetRepo = sheetRepo;
        this.categoryRepo = categoryRepo;
        this.hashtagService = hashtagService;
        this.sheetImageService = sheetImageService;
        this.universityService = universityService;
    }

    @Transactional
    public SheetResponse createSheet(CreateSheetRequest req, UUID sellerId) {

        Category category = categoryRepo.findById(req.categoryId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Category not found"));

        Sheet sheet = new Sheet();
        sheet.setUniversity(universityService.getByIdOrNull(req.universityId()));
        sheet.setTitle(req.title());
        sheet.setDescription(req.description());
        sheet.setPrice(req.price());
        sheet.setFileUrl(req.fileUrl());

        sheet.setCourseCode(req.courseCode());
        sheet.setCourseName(req.courseName());
        sheet.setStudyYear(req.studyYear());
        sheet.setAcademicYear(req.academicYear());

        sheet.setCategory(category);
        sheet.setSellerId(
                UUID.fromString("20a9ecd8-93c5-499c-b7cf-3045396d7121"));
        sheet.setStatus(SheetStatus.APPROVED);
        sheet.setIsPublished(true);

        // hashtags
        sheet.setHashtags(
                hashtagService.resolveHashtags(req.hashtags()));

        // preview images (delegated)
        sheetImageService.attachPreviewImages(
                sheet,
                req.previewUrls());

        sheetRepo.save(sheet);

        return new SheetResponse(
                sheet.getId(),
                sheet.getTitle(),
                sheet.getPrice(),
                sheet.getStatus().name());
    }

    public Page<SheetResponse> getSheets(int page, int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        return sheetRepo.findByStatus(SheetStatus.APPROVED, pageable)
                .map(sheet -> new SheetResponse(
                        sheet.getId(),
                        sheet.getTitle(),
                        sheet.getPrice(),
                        sheet.getStatus().name()));
    }
}
