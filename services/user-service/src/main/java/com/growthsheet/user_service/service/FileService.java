package com.growthsheet.user_service.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.cloudinary.Cloudinary;

@Service
public class FileService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    private final Cloudinary cloudinary;

    public FileService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    // ===============================
    // Upload สำหรับรูปทั่วไป
    // ===============================
    public String uploadImage(MultipartFile image) {
        return uploadImageToFolder(image, "growthsheet_assets/images_general");
    }

    // ===============================
    // Upload สำหรับรูปโปรไฟล์
    // ===============================
    public String uploadProfileImage(MultipartFile image) {
        return uploadImageToFolder(image, "growthsheet_assets/profile_images");
    }

    // ===============================
    // Upload สำหรับเอกสารผู้ขาย
    // ===============================
    public String uploadSellerImage(MultipartFile image) {
        return uploadImageToFolder(image, "growthsheet_assets/images_seller");
    }

    // ===============================
    // Core Upload Logic
    // ===============================
    private String uploadImageToFolder(MultipartFile image, String folder) {

        validateImage(image);

        try {
            Map<String, Object> uploadParams = new HashMap<>();
            uploadParams.put("folder", folder);
            uploadParams.put("quality", "auto");
            uploadParams.put("fetch_format", "auto");
            uploadParams.put("resource_type", "image");

            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult =
                    cloudinary.uploader().upload(image.getBytes(), uploadParams);

            return (String) uploadResult.get("secure_url");

        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Image upload failed: " + e.getMessage()
            );
        }
    }

    // ===============================
    // Validation
    // ===============================
    private void validateImage(MultipartFile image) {

        if (image == null || image.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Image is empty or missing"
            );
        }

        if (image.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "File size exceeds 5MB limit"
            );
        }

        String contentType = image.getContentType();

        if (!isImage(contentType)) {
            throw new ResponseStatusException(
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "Only image files are supported"
            );
        }
    }

    private boolean isImage(String contentType) {
        return contentType != null && contentType.startsWith("image/");
    }
}