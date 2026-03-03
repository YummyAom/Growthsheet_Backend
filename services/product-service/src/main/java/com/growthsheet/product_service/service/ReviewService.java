package com.growthsheet.product_service.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.growthsheet.product_service.config.client.OrderClient;
import com.growthsheet.product_service.config.client.UserClient;
import com.growthsheet.product_service.dto.UserDTO;
import com.growthsheet.product_service.dto.UserProfileResponse;
import com.growthsheet.product_service.dto.request.SheetReviewRequest;
import com.growthsheet.product_service.dto.response.ReviewResponse;
import com.growthsheet.product_service.entity.SheetReview;
import com.growthsheet.product_service.repository.ReviewRepository;
import com.growthsheet.product_service.repository.SheetRepository;

import jakarta.transaction.Transactional;

@Service
public class ReviewService {
    private final SheetRepository sheetRepo;
    private final ReviewRepository reviewRepo;
    private final OrderClient orderClient;
    private final UserClient userClient;

    // เราลบ SheetReview ออกจาก Constructor
    public ReviewService(
            SheetRepository sheetRepo,
            ReviewRepository reviewRepo,
            OrderClient orderClient,
            UserClient userClient) {
        this.sheetRepo = sheetRepo;
        this.reviewRepo = reviewRepo;
        this.orderClient = orderClient;
        this.userClient = userClient;
    }

    @Transactional
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

    public List<ReviewResponse> getReviewBySheetId(UUID sheetId) {
        List<SheetReview> reviews = reviewRepo.findBySheetId(sheetId);

        return reviews.stream().map(review -> {
            String userName = "Unknown User";
            UserProfileResponse user = userClient.getUserById(review.getUserId());
            if (user != null) {
                userName = user.getName();
            }
            return new ReviewResponse(
                    review.getId(),
                    review.getSheetId(),
                    new UserDTO(review.getUserId(), userName),
                    review.getComment(),
                    review.getRating());
        }).toList();
    }

    @Transactional
    public String updateReview(UUID userId, UUID reviewId, SheetReviewRequest request) {

        SheetReview review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("ไม่พบรีวิวที่ต้องการแก้ไข"));

        if (!review.getUserId().equals(userId)) {
            throw new RuntimeException("คุณไม่มีสิทธิ์แก้ไขรีวิวนี้");
        }

        review.setRating(request.rating());
        review.setComment(request.comment());
        reviewRepo.save(review);

        return "แก้ไขรีวิวเรียบร้อยแล้ว";
    }

    @Transactional
    public String deleteReview(UUID userId, UUID reviewId) {

        SheetReview review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("ไม่พบรีวิวที่ต้องการลบ"));

        if (!review.getUserId().equals(userId)) {
            throw new RuntimeException("คุณไม่มีสิทธิ์ลบรีวิวนี้");
        }

        reviewRepo.delete(review);

        return "ลบริวิวเรียบร้อยแล้ว";
    }
}
