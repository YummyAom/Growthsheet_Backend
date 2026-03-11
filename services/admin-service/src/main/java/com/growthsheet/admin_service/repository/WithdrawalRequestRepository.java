package com.growthsheet.admin_service.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.growthsheet.admin_service.entity.WithdrawStatus;
import com.growthsheet.admin_service.entity.WithdrawalRequest;

public interface WithdrawalRequestRepository extends JpaRepository<WithdrawalRequest, UUID> {

    @Query(
        value = """
            SELECT wr FROM WithdrawalRequest wr
            LEFT JOIN FETCH wr.sellerDetails
            WHERE wr.status = :status
            ORDER BY wr.createdAt DESC
            """,
        countQuery = """
            SELECT count(wr) FROM WithdrawalRequest wr
            WHERE wr.status = :status
            """
    )
    Page<WithdrawalRequest> findByStatus(@Param("status") WithdrawStatus status, Pageable pageable);

    // ✅ ดึงทั้งหมด
    @Query(
        value = """
            SELECT wr FROM WithdrawalRequest wr
            LEFT JOIN FETCH wr.sellerDetails
            ORDER BY wr.createdAt DESC
            """,
        countQuery = "SELECT count(wr) FROM WithdrawalRequest wr"
    )
    Page<WithdrawalRequest> findAllWithSeller(Pageable pageable);
}