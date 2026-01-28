package com.growthsheet.product_service.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "universities")
public class University {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nameTh;
    private String nameEn;

    // getters / setters
    public Long getId() {
        return id;
    }
    public String getNameEn() {
        return nameEn;
    }
    public String getNameTh() {
        return nameTh;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }
    public void setNameTh(String nameTh) {
        this.nameTh = nameTh;
    }
}
