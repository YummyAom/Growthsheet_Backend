package com.growthsheet.payment_service.repository;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.growthsheet.payment_service.entity.WithdrawalRequest;

public interface WithdrawalRequestRepository extends JpaRepository<WithdrawalRequest, UUID> {

    /**
     * ดึงรายการถอนเงินทั้งหมดของ seller (เรียงตามวันที่ล่าสุด)
     */
    Page<WithdrawalRequest> findBySellerIdOrderByCreatedAtDesc(UUID sellerId, Pageable pageable);

    /**
     * คำนวณยอดเงินที่ถอนไปแล้ว + รอโอน (status = APPROVED หรือ PENDING)
     */
    @Query("""
                SELECT COALESCE(SUM(wr.amount), 0)
                FROM WithdrawalRequest wr
                WHERE wr.sellerId = :sellerId
                AND wr.status IN (
                    com.growthsheet.payment_service.entity.WithdrawStatus.APPROVED,
                    com.growthsheet.payment_service.entity.WithdrawStatus.PENDING
                )
            """)
    BigDecimal sumWithdrawnBySellerId(@Param("sellerId") UUID sellerId);
}
