package com.growthsheet.notificaiton.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.growthsheet.notificaiton.entity.DeviceToken;

import java.util.List;
import java.util.UUID;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, UUID> {

    List<DeviceToken> findByUserId(UUID userId);

}