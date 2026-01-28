package com.growthsheet.product_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.growthsheet.product_service.entity.University;

@Repository
public interface UniversityRepository extends JpaRepository<University, Long> {
}
