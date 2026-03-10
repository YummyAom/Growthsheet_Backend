package com.growthsheet.product_service.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.growthsheet.product_service.dto.CategoryDTO;
import com.growthsheet.product_service.dto.SellerDTO;
import com.growthsheet.product_service.dto.UniversityDTO;

public record SheetCardResponse(
        UUID id,
        String title,
        String description,
        BigDecimal price,
        String image,
        UniversityDTO university,
        CategoryDTO category,
        List<String> hashtags,
        Double averageRating,
        Boolean isPublished,
        SellerDTO seller,
        Integer salesCount 
) {
    // --- 2. สร้าง Constructor รอง (Overloaded) ---
    // เผื่อให้โค้ดเก่าใน SheetAssembler / SheetCardMapper ยังทำงานได้ปกติ (ให้ Default เป็น 0 ไปก่อน)
    public SheetCardResponse(UUID id, String title, String description, BigDecimal price, String image, 
                             UniversityDTO university, CategoryDTO category, List<String> hashtags, 
                             Double averageRating, Boolean isPublished, SellerDTO seller) {
        this(id, title, description, price, image, university, category, hashtags, averageRating, isPublished, seller, 0);
    }

    // --- 3. สร้าง Helper Method เอาไว้ Copy & Update ค่า ---
    public SheetCardResponse withSalesCount(Integer salesCount) {
        return new SheetCardResponse(
                this.id(), this.title(), this.description(), this.price(), 
                this.image(), this.university(), this.category(), 
                this.hashtags(), this.averageRating(), this.isPublished(), 
                this.seller(), salesCount
        );
    }
}