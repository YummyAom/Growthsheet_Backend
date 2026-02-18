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

    private final Cloudinary cloudinary;
    public FileService(
        Cloudinary cloudinary
    ){
        this.cloudinary = cloudinary;
    }
    public String uploadImage(MultipartFile image) {

        if (image == null || image.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Image is empty or missing"
            );
        }

        String contentType = image.getContentType();

        if (!isImage(contentType)) {
            throw new ResponseStatusException(
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "Only image files are supported"
            );
        }

        try {
            Map<String, Object> uploadParams = new HashMap<>();
            uploadParams.put("folder", "growthsheet_assets/images_seller");
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

    private boolean isImage(String contentType) {
        return contentType != null && contentType.startsWith("image/");
    }
}
