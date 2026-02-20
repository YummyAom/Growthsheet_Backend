package com.growthsheet.user_service.dto.response;

import com.growthsheet.user_service.dto.UniversityDTO;
import com.growthsheet.user_service.entity.User;

import lombok.Getter;

@Getter
public class UserProfileResponseDTO {

    private Long id;
    private String name;
    private String email;
    private String userPhotoUrl;
    private Integer studentYear;
    private String faculty;
    private String role;
    private UniversityDTO university;

    public UserProfileResponseDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.userPhotoUrl = user.getUserPhotoUrl();
        this.studentYear = user.getStudentYear();
        this.faculty = user.getFaculty();
        this.role = user.getRole().name();
        this.university = new UniversityDTO(user.getUniversity());
    }
}