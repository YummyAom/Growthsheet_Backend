package com.growthsheet.user_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.time.LocalDateTime;

@Entity
@Table(name = "seller_details")
@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerDetail {

    @Id
    private UUID id; 

    @OneToOne
    @MapsId 
    @JoinColumn(name = "user_id")
    private User user;

    private String penName;
    
    @Column(nullable = false)
    private String fullName; 

    private String university;
    private String studentId;

    private String studentCardUrl;
    private String selfieWithCardUrl;

    private String phoneNumber;
    private String contactEmail;

    private String bankName;
    private String bankAccountNumber;
    private String bankAccountOwner; 

    @Builder.Default
    private boolean isVerified = false; 

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}