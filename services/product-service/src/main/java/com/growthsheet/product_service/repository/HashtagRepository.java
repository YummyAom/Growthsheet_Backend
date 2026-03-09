package com.growthsheet.product_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.growthsheet.product_service.entity.Hashtag;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    Optional<Hashtag> findByName(String name);

    @Query("SELECT h.name FROM Hashtag h ORDER BY h.name")
    List<String> findAllTagNames();

    @Query("""
            SELECT h.name
            FROM SheetHashtag sh
            JOIN sh.hashtag h
            GROUP BY h.name
            ORDER BY COUNT(sh) DESC
            """)
    List<String> findPopularTags(Pageable pageable);
}
