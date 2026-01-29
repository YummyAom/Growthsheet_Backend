package com.growthsheet.product_service.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.growthsheet.product_service.entity.University;
import com.growthsheet.product_service.repository.UniversityRepository;

@Service
public class UniversityService {

    private final UniversityRepository universityRepo;

    public UniversityService(UniversityRepository universityRepo) {
        this.universityRepo = universityRepo;
    }

    // อันนี้ return null ได้จริง
    public University findOrNull(Long id) {
        if (id == null)
            return null;
        return universityRepo.findById(id).orElse(null);
    }

    // อันนี้บังคับต้องมี
    public University getById(Long id) {
        return universityRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "University not found"));
    }

    public List<University> getAll() {
        return universityRepo.findAll();
    }
}
