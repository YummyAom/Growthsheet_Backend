package com.growthsheet.product_service.controller;

import java.util.UUID;
import jakarta.validation.Valid; // สำหรับตรวจสอบ @NotBlank, @NotNull
import org.springframework.web.bind.annotation.*;

import com.growthsheet.product_service.dto.request.SheetReviewRequest;
import com.growthsheet.product_service.service.ReviewService;

@RestController
@RequestMapping("/api/products")
public class SheetReviewController {

    private final ReviewService reviewService;

    public SheetReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/{sheetId}/reviews") 
    public String createReview(
        @RequestHeader("X-USER-ID") UUID userId,
        @PathVariable UUID sheetId,
        @Valid @RequestBody SheetReviewRequest request 
    ) {
        return reviewService.createReview(userId, sheetId, request);
    }

    // @GetMapping("/{sheetId}/reviews") 
    // public String getReviewBySheetId(
    //     @PathVariable UUID sheetId
    // ){
    //     return "rr";
    // }
}