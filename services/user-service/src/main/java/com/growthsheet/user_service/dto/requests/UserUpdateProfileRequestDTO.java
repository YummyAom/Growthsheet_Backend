package com.growthsheet.user_service.dto.requests;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateProfileRequestDTO {

    @NotBlank(message = "กรุณากรอกชื่อ")
    private String name;

    @NotNull(message = "กรุณาเลือกมหาวิทยาลัย")
    private Long universityId;

    @NotBlank(message = "กรุณากรอกคณะ")
    private String faculty;

    @NotNull(message = "กรุณาเลือกชั้นปี")
    @Min(value = 1, message = "ชั้นปีต้องอยู่ระหว่าง 1-4")
    @Max(value = 4, message = "ชั้นปีต้องอยู่ระหว่าง 1-4")
    private Integer studentYear;
}