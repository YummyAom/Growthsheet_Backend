package com.growthsheet.product_service.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.cloudinary.Cloudinary;

@Service
public class FileService {

    @Autowired
    private Cloudinary cloudinary;

    public Map<String, Object> uploadFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty or missing");
        }

        String contentType = file.getContentType();
        Map<String, Object> result = new HashMap<>();

        int pageCount = 0;
        String fileType;

        try {
            if (isPdf(contentType)) {
                pageCount = getPdfPageCount(file);
                fileType = "PDF";
            } else if (isImage(contentType)) {
                fileType = "IMAGE";
            } else {
                throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                        "Only PDF and Image files are supported");
            }

            Map uploadResult = uploadToCloudinary(file, fileType);

            result.put("url", uploadResult.get("secure_url"));
            result.put("pageCount", pageCount);
            result.put("fileType", fileType);
            result.put("fileName", file.getOriginalFilename());

            return result;

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "File processing failed: " + e.getMessage());
        }
    }

    public List<String> uploadImage(List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image is empty or missing");
        }

        List<String> imageUrls = new ArrayList<>();
        try {
            for (MultipartFile image : images) {
                // ตรวจสอบว่าเป็นรูปภาพจริงหรือไม่
                String contentType = image.getContentType();
                if (!isImage(contentType)) {
                    throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                            "File " + image.getOriginalFilename() + " is not an image");
                }

                // เรียกใช้ฟังก์ชันอัปโหลดไป Cloudinary ที่คุณมีอยู่แล้ว
                // โดยระบุประเภทเป็น "IMAGE" เพื่อให้แยกเข้าโฟลเดอร์ /images
                Map<String, Object> uploadResult = uploadToCloudinary(image, "IMAGE");

                // ดึงเฉพาะ secure_url มาเก็บไว้ใน List
                imageUrls.add((String) uploadResult.get("secure_url"));
            }

            return imageUrls;

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Image processing failed: " + e.getMessage());
        }
    }

    // --- Private Helper Methods ---

    private boolean isPdf(String contentType) {
        return contentType != null && contentType.equalsIgnoreCase("application/pdf");
    }

    private boolean isImage(String contentType) {
        return contentType != null && contentType.startsWith("image/");
    }

    private int getPdfPageCount(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            return document.getNumberOfPages();
        }
    }

    private Map<String, Object> uploadToCloudinary(
            MultipartFile file,
            String fileType

    ) throws IOException {
        Map<String, Object> uploadParams = new HashMap<>();

        if (fileType == "PDF") {
            uploadParams.put("folder", "growthsheet_assets/pdf");
        } else if (fileType == "IMAGE") {
            uploadParams.put("folder", "growthsheet_assets/images");
            uploadParams.put("quality", "auto");
            uploadParams.put("fetch_format", "auto");
        }

        uploadParams.put("resource_type", "auto");

        @SuppressWarnings("unchecked")
        Map<String, Object> uploadResult = (Map<String, Object>) cloudinary.uploader().upload(file.getBytes(),
                uploadParams);

        return uploadResult;
    }
}