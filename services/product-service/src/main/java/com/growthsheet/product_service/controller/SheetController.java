package com.growthsheet.product_service.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.growthsheet.product_service.dto.request.CreateSheetRequest;
import com.growthsheet.product_service.dto.response.ProductResponseDTO;
import com.growthsheet.product_service.dto.response.SheetResponse;
import com.growthsheet.product_service.service.SheetService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class SheetController {
    private final SheetService sheetService;

    public SheetController(SheetService sheetService) {
        this.sheetService = sheetService;
    }

    @GetMapping("/")
    public String hello() {
        return "Hello products";
    }

    @PostMapping("/create")
    public ResponseEntity<SheetResponse> createSheet(
            @RequestHeader("X-USER-ID") UUID sellerId,
            @Valid @RequestBody CreateSheetRequest req) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(sheetService.createSheet(req, sellerId));
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> getSheets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(sheetService.getSheets(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getSheetById(
            @PathVariable UUID id) {

        return ResponseEntity.ok(sheetService.getSheet(id));
    }

}
