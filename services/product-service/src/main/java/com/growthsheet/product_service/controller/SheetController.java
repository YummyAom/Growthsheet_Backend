package com.growthsheet.product_service.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.growthsheet.product_service.dto.request.CreateSheetRequest;
import com.growthsheet.product_service.dto.response.ProductResponseDTO;
import com.growthsheet.product_service.dto.response.SheetCardResponse;
import com.growthsheet.product_service.dto.response.SheetResponse;
import com.growthsheet.product_service.service.FileService;
import com.growthsheet.product_service.service.SheetLikeService;
import com.growthsheet.product_service.service.SheetService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class SheetController {
    private final SheetService sheetService;
    private final FileService fileService;
    private final SheetLikeService sheetLikeService;

    public SheetController(
            SheetService sheetService,
            FileService fileService,
            SheetLikeService sheetLikeService) {
        this.sheetService = sheetService;
        this.fileService = fileService;
        this.sheetLikeService = sheetLikeService;
    }

    @GetMapping("/")
    public String hello() {
        return "Hello products";
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SheetResponse> createSheet(
            @RequestHeader("X-USER-ID") UUID sellerId,
            @Valid @RequestPart("data") CreateSheetRequest req,
            @RequestPart("filePDF") MultipartFile pdfFile,
            @RequestPart("previewImage") List<MultipartFile> previewImages) {

        Map<String, Object> pdf = fileService.uploadFile(pdfFile);
        List<String> images = fileService.uploadImage(previewImages);

        SheetResponse response = sheetService.createSheet(req, sellerId, pdf, images);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/files/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(fileService.uploadFile(file));
    }

    // @GetMapping
    // public ResponseEntity<Page<SheetCardResponse>> getSheets(
    // @RequestParam(defaultValue = "0") int page,
    // @RequestParam(defaultValue = "10") int size) {

    // return ResponseEntity.ok(sheetService.getSheets(page, size));
    // }

    @GetMapping
    public ResponseEntity<Page<SheetCardResponse>> getSheets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "latest") String sort) {

        return ResponseEntity.ok(
                sheetService.getSheets(page, size, sort));
    }

    @GetMapping("/{sheetId}")
    public ResponseEntity<ProductResponseDTO> getSheetById(
            @PathVariable UUID sheetId) {
        return ResponseEntity.ok(sheetService.getSheet(sheetId));
    }

    @GetMapping("/sellers/{sellerId}/sheets")
    public ResponseEntity<Page<SheetCardResponse>> getSheetPageByUserId(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Boolean isPublished,
            @PathVariable UUID sellerId) {

        return ResponseEntity.ok(
                sheetService.findSheetPageByUserId(sellerId, page, size, isPublished));
    }

    @PostMapping("/{sheetId}/like")
    public ResponseEntity<?> toggleLike(
            @PathVariable UUID sheetId,
            @RequestHeader("X-USER-ID") UUID userId) {

        boolean liked = sheetLikeService.toggleLike(sheetId, userId);

        return ResponseEntity.ok(
                Map.of("liked", liked));
    }

    @GetMapping("/liked")
    public ResponseEntity<Page<SheetCardResponse>> getLikedSheets(
            @RequestHeader("X-USER-ID") UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                sheetLikeService.getLikedSheets(userId, page, size));
    }

}
