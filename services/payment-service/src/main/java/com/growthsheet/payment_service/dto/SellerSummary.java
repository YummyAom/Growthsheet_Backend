package com.growthsheet.payment_service.dto;

import java.math.BigDecimal;
import java.util.UUID;

public interface SellerSummary {
    UUID getSeller_id();
    String getSeller_name();
    BigDecimal getTotal();
    String getSheet_names();
}