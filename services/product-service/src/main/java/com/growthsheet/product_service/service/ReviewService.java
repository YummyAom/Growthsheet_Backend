package com.growthsheet.product_service.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.growthsheet.product_service.config.client.OrderClient;
import com.growthsheet.product_service.dto.client.OrderResponse;
import com.growthsheet.product_service.dto.request.SheetReviewRequest;
import com.growthsheet.product_service.entity.SheetReview;
import com.growthsheet.product_service.repository.ReviewRepository;
import com.growthsheet.product_service.repository.SheetRepository;

@Service
public class ReviewService {
    private final SheetRepository sheetRepo;
    private final ReviewRepository reviewRepo;
    private final OrderClient orderClient;

    // เราลบ SheetReview ออกจาก Constructor
    public ReviewService(
            SheetRepository sheetRepo,
            ReviewRepository reviewRepo,
            OrderClient orderClient) {
        this.sheetRepo = sheetRepo;
        this.reviewRepo = reviewRepo;
        this.orderClient = orderClient;
    }

    public String createReview(UUID userId, UUID sheetId, SheetReviewRequest request) {
        if (!sheetRepo.existsById(sheetId)) {
            return "ไม่พบชีทสรุปชุดนี้";
        }

        // List<OrderResponse> orders = orderClient.getOrdersByUser(userId);
        SheetReview review = new SheetReview();
        review.setComment(request.comment());
        review.setRating(request.rating());
        reviewRepo.save(review);

        return "บันทึกรีวิวเรียบร้อยแล้ว";
    }
}
