package com.growthsheet.admin_service.dto;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellerDTO {
    private UUID id;      // ต้องชื่อ id
    private String name;  // ต้องชื่อ name
}