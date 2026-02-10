package com.growthsheet.product_service.service;

import org.springframework.stereotype.Component;

import com.growthsheet.product_service.dto.SellerDTO;
import com.growthsheet.product_service.dto.response.SheetCardResponse;
import com.growthsheet.product_service.entity.Sheet;
import com.growthsheet.product_service.mapper.SheetCardMapper;


@Component
public class SheetAssembler {

    private final SellerService sellerService;
    private final SheetCardMapper mapper;
    
    public SheetAssembler(SellerService sellerService, SheetCardMapper mapper){
        this.sellerService = sellerService;
        this.mapper = mapper;
    }

    public SheetCardResponse assemble(Sheet sheet) {
        SellerDTO seller = sellerService.getSeller(sheet.getSellerId());
        return mapper.toResponse(sheet, seller);
    }
}