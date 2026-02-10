package com.growthsheet.product_service.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.growthsheet.product_service.dto.CategoryDTO;
import com.growthsheet.product_service.dto.SellerDTO;
import com.growthsheet.product_service.dto.UniversityDTO;
import com.growthsheet.product_service.dto.response.SheetCardResponse;
import com.growthsheet.product_service.entity.Hashtag;
import com.growthsheet.product_service.entity.Sheet;

@Component
public class SheetCardMapper {

    public SheetCardResponse toResponse(
            Sheet sheet,
            SellerDTO seller
    ) {
        return new SheetCardResponse(
                sheet.getId(),
                sheet.getTitle(),
                sheet.getDescription(),
                sheet.getPrice(),
                extractFirstImage(sheet),
                mapUniversity(sheet),
                mapCategory(sheet),
                mapHashtags(sheet),
                extractRating(sheet),
                sheet.getIsPublished(),
                seller
        );
    }

    private String extractFirstImage(Sheet sheet) {
        if (sheet.getPreviewImages() == null || sheet.getPreviewImages().isEmpty())
            return null;

        return sheet.getPreviewImages().get(0).getImageUrl();
    }

    private UniversityDTO mapUniversity(Sheet sheet) {
        if (sheet.getUniversity() == null) return null;

        return new UniversityDTO(
                sheet.getUniversity().getId(),
                sheet.getUniversity().getNameEn()
        );
    }

    private CategoryDTO mapCategory(Sheet sheet) {
        if (sheet.getCategory() == null) return null;

        return new CategoryDTO(
                sheet.getCategory().getId(),
                sheet.getCategory().getName()
        );
    }

    private List<String> mapHashtags(Sheet sheet) {
        return sheet.getHashtags()
                .stream()
                .map(Hashtag::getName)
                .toList();
    }

    private Double extractRating(Sheet sheet) {
        return sheet.getAverageRating() == null
                ? 0.0
                : sheet.getAverageRating().doubleValue();
    }
}
