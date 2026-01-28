package com.growthsheet.product_service.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.growthsheet.product_service.dto.request.CreateSheetRequest;
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

    @PostMapping
    public ResponseEntity<SheetResponse> createSheet(
            @RequestHeader("X-USER-ID") UUID sellerId,
            @Valid @RequestBody CreateSheetRequest request) {
        SheetResponse response = sheetService.create(request, sellerId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

}
