package com.growthsheet.product_service.service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.growthsheet.product_service.entity.Hashtag;
import com.growthsheet.product_service.repository.HashtagRepository;

@Service
public class HashtagService {

    private final HashtagRepository hashtagRepo;

    public HashtagService(HashtagRepository hashtagRepo) {
        this.hashtagRepo = hashtagRepo;
    }

    public Set<Hashtag> resolveHashtags(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return Collections.emptySet();
        }

        return tags.stream()
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(tag -> !tag.isBlank())
                .distinct()              // กันซ้ำ
                .map(this::findOrCreate)
                .collect(Collectors.toSet());
    }

    private Hashtag findOrCreate(String name) {
        return hashtagRepo.findByName(name)
                .orElseGet(() -> {
                    Hashtag h = new Hashtag();
                    h.setName(name);
                    return hashtagRepo.save(h);
                });
    }
}
