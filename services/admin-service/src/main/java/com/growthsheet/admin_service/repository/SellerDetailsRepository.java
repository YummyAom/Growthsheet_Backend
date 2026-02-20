package com.growthsheet.admin_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.growthsheet.admin_service.entity.SellerDetails;

public interface SellerDetailsRepository extends JpaRepository<SellerDetails, UUID> {

    @Query(value = """
            SELECT * FROM seller_details
            WHERE is_verified = :status
            ORDER BY created_at DESC
            """, countQuery = """
            SELECT count(*) FROM seller_details
            WHERE is_verified = :status
            """, nativeQuery = true)
    Page<SellerDetails> findByStatus(String status, Pageable pageable);

        Optional<SellerDetails> findByUser_id(UUID user_id);

}