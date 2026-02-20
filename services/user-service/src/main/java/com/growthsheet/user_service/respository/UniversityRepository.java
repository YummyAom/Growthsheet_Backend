package com.growthsheet.user_service.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.growthsheet.user_service.entity.University;

public interface UniversityRepository extends JpaRepository<University, Long> {

}