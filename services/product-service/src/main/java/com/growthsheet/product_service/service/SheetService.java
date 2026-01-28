package com.growthsheet.product_service.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.growthsheet.product_service.dto.request.CreateSheetRequest;
import com.growthsheet.product_service.dto.response.SheetResponse;
import com.growthsheet.product_service.entity.Category;
import com.growthsheet.product_service.entity.Sheet;
import com.growthsheet.product_service.repository.CategoryRepository;
import com.growthsheet.product_service.repository.SheetRepository;

@Service
public class SheetService {

    private final SheetRepository sheetRepo;
    private final CategoryRepository categoryRepo;

    public SheetService(SheetRepository sheetRepo, CategoryRepository categoryRepo) {
        this.sheetRepo = sheetRepo;
        this.categoryRepo = categoryRepo;
    }

    public SheetResponse create(CreateSheetRequest req, UUID sellerId) {

        Category category = categoryRepo.findById(req.categoryId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Category not found"));

        Sheet sheet = new Sheet();
        sheet.setTitle(req.title());
        sheet.setDescription(req.description());
        sheet.setPrice(req.price());
        sheet.setFileUrl(req.fileUrl());
        sheet.setCategory(category);
        sheet.setSellerId(sellerId);

        sheetRepo.save(sheet);

        return new SheetResponse(
                sheet.getId(),
                sheet.getTitle(),
                sheet.getPrice(),
                sheet.getStatus().name());
    }
}
