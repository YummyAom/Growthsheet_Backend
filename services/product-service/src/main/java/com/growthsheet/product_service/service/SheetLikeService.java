package com.growthsheet.product_service.service;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.growthsheet.product_service.dto.SellerDTO;
import com.growthsheet.product_service.dto.response.SheetCardResponse;
import com.growthsheet.product_service.entity.Sheet;
import com.growthsheet.product_service.entity.SheetLike;
import com.growthsheet.product_service.mapper.SheetCardMapper;
import com.growthsheet.product_service.repository.SheetLikeRepository;
import com.growthsheet.product_service.repository.SheetRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SheetLikeService {

    private final SheetLikeRepository sheetLikeRepository;
    private final SheetRepository sheetRepository;
    private final SheetCardMapper sheetCardMapper;

    @Transactional
    public boolean toggleLike(UUID sheetId, UUID userId) {

        if (!sheetRepository.existsById(sheetId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Sheet not found");
        }

        if (sheetLikeRepository.existsBySheetIdAndUserId(sheetId, userId)) {

            sheetLikeRepository.deleteBySheetIdAndUserId(sheetId, userId);
            return false;

        } else {

            SheetLike like = new SheetLike();
            like.setSheetId(sheetId);
            like.setUserId(userId);
            sheetLikeRepository.save(like);

            return true;
        }
    }

    public Page<SheetCardResponse> getLikedSheets(UUID userId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Sheet> sheetPage = sheetRepository.findLikedSheets(userId, pageable);

        return sheetPage.map(sheet -> sheetCardMapper.toResponse(
                sheet,
                new SellerDTO(sheet.getSellerId(), null)));
    }
}
