package com.growthsheet.admin_service.dto;

public class UpdateUserRoleRequest {
    private String role;

    public UpdateUserRoleRequest(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}