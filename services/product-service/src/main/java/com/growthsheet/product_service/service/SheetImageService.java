package com.growthsheet.product_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.growthsheet.product_service.entity.Sheet;
import com.growthsheet.product_service.entity.SheetImage;

@Service
public class SheetImageService {

    public void attachPreviewImages(Sheet sheet, List<String> previewUrls) {

        if (previewUrls == null || previewUrls.isEmpty()) {
            return;
        }

        int order = 0;
        for (String url : previewUrls) {
            SheetImage img = new SheetImage();
            img.setImageUrl(url);
            img.setSortOrder(order++);
            img.setSheet(sheet);
            sheet.getPreviewImages().add(img);
        }
    }
}
