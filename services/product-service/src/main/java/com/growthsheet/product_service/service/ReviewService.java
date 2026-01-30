package com.growthsheet.product_service.service;

import java.util.UUID;

import org.hibernate.query.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.growthsheet.product_service.dto.response.SheetReviewResponseDTO;

// public class ReviewService {
//     public Page<SheetReviewResponseDTO> getByProductId(UUID productId, int page, int size) {
//         Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
//         Object reviewRepository;
//         return reviewRepository.findBySheetId(productId, pageable)
//                 .map(this::toDTO);
//     }
// }
