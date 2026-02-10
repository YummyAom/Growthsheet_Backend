package com.growthsheet.product_service.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.growthsheet.product_service.dto.SellerDTO;
import com.growthsheet.product_service.repository.UserRepository;
@Service
public class SellerService {

    private final UserRepository userRepo;

    public SellerService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }
    
    public SellerDTO getSeller(UUID sellerId) {
        return userRepo.findById(sellerId)
                .map(u -> new SellerDTO(u.getId(), u.getName()))
                .orElse(null);
    }
}