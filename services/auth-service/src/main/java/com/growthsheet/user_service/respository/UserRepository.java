package com.growthsheet.user_service.respository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.growthsheet.user_service.entity.User;

public interface UserRepository extends JpaRepository<User, UUID> {
        Optional<User> findByEmail(String email);
}
