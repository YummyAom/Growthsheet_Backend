package com.growthsheet.product_service.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.growthsheet.product_service.dto.response.SheetReviewResponseDTO;

// @RestController
// @RequestMapping("/api/products")
// public class SheetReviewController {
//     @GetMapping("/{id}/reviews")
//     public Page<SheetReviewResponseDTO> getReviews(
//             @PathVariable UUID id,
//             @RequestParam(defaultValue = "0") int page,
//             @RequestParam(defaultValue = "5") int size) {
//                 return sheetService.getSheets(id, page, size);
//     }
// }
