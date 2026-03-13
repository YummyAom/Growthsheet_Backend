package com.growthsheet.product_service.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.growthsheet.product_service.config.client.NotificationClient;
import com.growthsheet.product_service.config.client.OrderClient;
import com.growthsheet.product_service.config.client.UserClient;
import com.growthsheet.product_service.dto.PageResponse;
import com.growthsheet.product_service.dto.UserDTO;
import com.growthsheet.product_service.dto.UserProfileResponse;
import com.growthsheet.product_service.dto.client.OrderResponse;
import com.growthsheet.product_service.dto.request.NotificationRequest;
import com.growthsheet.product_service.dto.request.SheetImageRequest;
import com.growthsheet.product_service.dto.request.SheetReviewRequest;
import com.growthsheet.product_service.dto.response.PendingReviewResponse;
import com.growthsheet.product_service.dto.response.ReviewResponse;
import com.growthsheet.product_service.dto.response.SellerReviewResponse;
import com.growthsheet.product_service.entity.Sheet;
import com.growthsheet.product_service.entity.SheetImage;
import com.growthsheet.product_service.entity.SheetReview;
import com.growthsheet.product_service.repository.ReviewRepository;
import com.growthsheet.product_service.repository.SheetImageRepository;
import com.growthsheet.product_service.repository.SheetRepository;

import jakarta.transaction.Transactional;

@Service
public class ReviewService {
    private final SheetRepository sheetRepo;
    private final SheetImageRepository sheetImageRepo;
    private final ReviewRepository reviewRepo;
    private final OrderClient orderClient;
    private final UserClient userClient;
    private final NotificationClient notificationClient;

    // เราลบ SheetReview ออกจาก Constructor
    public ReviewService(
            SheetRepository sheetRepo,
            ReviewRepository reviewRepo,
            OrderClient orderClient,
            UserClient userClient,
            SheetImageRepository sheetImageRepo,
            NotificationClient notificationClient
        ) {
        this.sheetRepo = sheetRepo;
        this.reviewRepo = reviewRepo;
        this.orderClient = orderClient;
        this.userClient = userClient;
        this.sheetImageRepo = sheetImageRepo;
        this.notificationClient = notificationClient;
    }

    @Transactional
    public String createReview(UUID userId, UUID sheetId, SheetReviewRequest request) {
        if (!sheetRepo.existsById(sheetId)) {
            return "ไม่พบชีทสรุปชุดนี้";
        }

        // List<OrderResponse> orders = orderClient.getOrdersByUser(userId);
        SheetReview review = new SheetReview();
        review.setSheetId(sheetId); // ⭐ เพิ่ม
        review.setUserId(userId); // ⭐ เพิ่ม
        review.setComment(request.comment());
        review.setRating(request.rating());
        reviewRepo.save(review);
        updateSheetRating(sheetId);

        Sheet sheet = sheetRepo.findById(sheetId)
            .orElseThrow(() -> new RuntimeException("ไม่พบชีทสรุปชุดนี้"));
        NotificationRequest noti = new NotificationRequest();
        noti.setUserId(sheet.getSellerId());
        noti.setTitle("มีรีวิวใหม่");
        noti.setMessage("ชีท " + sheet.getTitle() + " ได้รับรีวิวใหม่ ⭐");
        notificationClient.createNotification(null);
        return "บันทึกรีวิวเรียบร้อยแล้ว";
    }

    private void updateSheetRating(UUID sheetId) {

        Double avg = reviewRepo.getAverageRatingBySheetId(sheetId);
        Long count = reviewRepo.countBySheetId(sheetId);

        sheetRepo.findById(sheetId).ifPresent(sheet -> {

            BigDecimal avgRating = avg != null
                    ? BigDecimal.valueOf(avg)
                    : BigDecimal.ZERO;

            sheet.setAverageRating(avgRating);
            sheet.setReviewCount(count.intValue());

            sheetRepo.save(sheet);
        });
    }

    public List<ReviewResponse> getReviewBySheetId(UUID sheetId) {
        List<SheetReview> reviews = reviewRepo.findBySheetId(sheetId);

        return reviews.stream().map(review -> {
            UserProfileResponse user = userClient.getUserById(review.getUserId());

            return new ReviewResponse(
                    review.getId(),
                    review.getSheetId(),
                    new UserDTO(
                            review.getUserId(),
                            user != null ? user.getName() : "Unknown User",
                            user != null ? user.getUserPhotoUrl() : null),
                    review.getComment(),
                    review.getRating(),
                    review.getCreatedAt());
        }).toList();
    }

    public Page<SellerReviewResponse> getReviewsBySeller(UUID sellerId, Pageable pageable) {

        List<UUID> sheetIds = sheetRepo.findBySellerId(sellerId)
                .stream()
                .map(Sheet::getId)
                .toList();

        if (sheetIds.isEmpty()) {
            return Page.empty();
        }

        // Map sheetId -> Sheet ไว้ก่อนเพื่อไม่ต้อง query ซ้ำในแต่ละ review
        Map<UUID, Sheet> sheetMap = sheetRepo.findBySellerId(sellerId)
                .stream()
                .collect(Collectors.toMap(Sheet::getId, s -> s));

        return reviewRepo.findBySheetIdIn(sheetIds, pageable)
                .map(review -> {
                    Sheet sheet = sheetMap.get(review.getSheetId());

                    String thumbnailUrl = sheetImageRepo
                            .findFirstBySheetIdOrderBySortOrderAsc(review.getSheetId())
                            .map(SheetImage::getImageUrl)
                            .orElse(null);

                    UserProfileResponse reviewer = userClient.getUserById(review.getUserId());

                    return new SellerReviewResponse(
                            review.getSheetId(),
                            sheet != null ? sheet.getTitle() : "Unknown Sheet",
                            thumbnailUrl,
                            review.getId(),
                            review.getRating(),
                            review.getComment(),
                            review.getCreatedAt(),
                            review.getUserId(),
                            reviewer != null ? reviewer.getName() : "Unknown User",
                            reviewer != null ? reviewer.getUserPhotoUrl() : null);
                });
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

    public List<PendingReviewResponse> getPendingReviews(UUID userId) {

        List<PendingReviewResponse> pendingReviews = new ArrayList<>();
        Set<UUID> addedSheetIds = new HashSet<>();

        PageResponse<OrderResponse> orders = orderClient.getPaidOrders(userId, Pageable.unpaged());

        orders.getContent().forEach(order -> {

            order.getItems().forEach(item -> {

                UUID sheetId = item.getSheetId();

                if (addedSheetIds.contains(sheetId)) {
                    return;
                }

                boolean reviewed = reviewRepo.existsBySheetIdAndUserId(sheetId, userId);

                if (!reviewed) {

                    sheetRepo.findById(sheetId).ifPresent(sheet -> {

                        PendingReviewResponse response = new PendingReviewResponse();

                        response.setSheetId(sheet.getId());
                        response.setTitle(sheet.getTitle());
                        response.setDescription(sheet.getDescription());
                        response.setAverageRating(sheet.getAverageRating());
                        response.setCategory(sheet.getCategory().getName());
                        response.setCourseName(sheet.getCourseName());

                        sheetImageRepo
                                .findFirstBySheetIdOrderBySortOrderAsc(sheet.getId())
                                .ifPresent(img -> response.setThumbnailUrl(img.getImageUrl()));

                        pendingReviews.add(response);

                        addedSheetIds.add(sheetId);
                    });
                }
            });
        });

        return pendingReviews;
    }

}
