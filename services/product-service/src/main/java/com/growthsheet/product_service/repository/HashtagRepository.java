package com.growthsheet.product_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.growthsheet.product_service.entity.Hashtag;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    Optional<Hashtag> findByName(String name);
}
