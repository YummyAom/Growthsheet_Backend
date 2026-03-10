package com.growthsheet.product_service.controller;

import java.util.List;
import java.util.UUID;
import jakarta.validation.Valid; // สำหรับตรวจสอบ @NotBlank, @NotNull
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import org.springframework.web.bind.annotation.*;

import com.growthsheet.product_service.dto.request.SheetReviewRequest;
import com.growthsheet.product_service.dto.response.PendingReviewResponse;
import com.growthsheet.product_service.dto.response.ReviewResponse; 
import com.growthsheet.product_service.dto.response.SellerReviewResponse;
import com.growthsheet.product_service.service.ReviewService;

@RestController
@RequestMapping("/api/products")
public class SheetReviewController {

    private final ReviewService reviewService;

    public SheetReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/reviews/pending")
    public List<PendingReviewResponse> getPendingReviews(
            @RequestHeader("X-USER-ID") UUID userId) {
        return reviewService.getPendingReviews(userId);
    }

    @PostMapping("/{sheetId}/reviews")
    public String createReview(
            @RequestHeader("X-USER-ID") UUID userId,
            @PathVariable UUID sheetId,
            @Valid @RequestBody SheetReviewRequest request) {
        return reviewService.createReview(userId, sheetId, request);
    }

    @GetMapping("/{sheetId}/reviews")
    public List<ReviewResponse> getReviewBySheetId(
            @PathVariable UUID sheetId) {
        return reviewService.getReviewBySheetId(sheetId);
    }

    @PutMapping("/reviews/{reviewId}")
    public String updateReview(
            @RequestHeader("X-USER-ID") UUID userId,
            @PathVariable UUID reviewId,
            @Valid @RequestBody SheetReviewRequest request) {
        return reviewService.updateReview(userId, reviewId, request);
    }

    // ลบริวิว
    @DeleteMapping("/reviews/{reviewId}")
    public String deleteReview(
            @RequestHeader("X-USER-ID") UUID userId,
            @PathVariable UUID reviewId) {
        return reviewService.deleteReview(userId, reviewId);
    }

    @GetMapping("/reviews/pendding")
    public List<PendingReviewResponse> getPenddingReview(
            @RequestHeader("X-USER-ID") UUID userId) {
        return reviewService.getPendingReviews(userId);
    }

    @GetMapping("/reviews/seller")
    public Page<SellerReviewResponse> getSellerReviews(
            @RequestHeader("X-USER-ID") UUID sellerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return reviewService.getReviewsBySeller(sellerId, pageable);
    }
}