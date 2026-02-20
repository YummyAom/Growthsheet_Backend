package com.growthsheet.user_service.dto;

import com.growthsheet.user_service.entity.University;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class UniversityDTO {

    private Long id;
    private String nameTh;
    private String nameEn;

    public UniversityDTO(University university) {
        if (university != null) {
            this.id = university.getId();
            this.nameTh = university.getNameTh();
            this.nameEn = university.getNameEn();
        }
    }

    // Getter
}