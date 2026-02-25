package com.growthsheet.user_service.dto.requests;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UserUpdateProfileRequestDTO {

    @NotBlank(message = "กรุณากรอกชื่อ")
    private String name;

    // เปลี่ยนจาก Long เป็น String และใช้ @NotBlank แทน @NotNull
    @NotBlank(message = "กรุณากรอกชื่อมหาวิทยาลัย")
    private String university;

    @NotBlank(message = "กรุณากรอกคณะ")
    private String faculty;

    @NotNull(message = "กรุณาเลือกชั้นปี")
    @Min(value = 1, message = "ชั้นปีต้องอยู่ระหว่าง 1-4")
    @Max(value = 4, message = "ชั้นปีต้องอยู่ระหว่าง 1-4")
    private Integer studentYear;

    // --- Getter & Setter Methods ---

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // เปลี่ยนชื่อ Method ให้ตรงกับชื่อ Field 'university'
    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public Integer getStudentYear() {
        return studentYear;
    }

    public void setStudentYear(Integer studentYear) {
        this.studentYear = studentYear;
    }
}