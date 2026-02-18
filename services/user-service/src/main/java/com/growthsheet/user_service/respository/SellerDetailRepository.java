package com.growthsheet.user_service.respository;

import com.growthsheet.user_service.entity.SellerDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SellerDetailRepository extends JpaRepository<SellerDetail, UUID> {
}