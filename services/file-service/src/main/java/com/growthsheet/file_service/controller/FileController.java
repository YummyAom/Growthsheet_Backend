package com.growthsheet.file_service.controller;

import com.growthsheet.file_service.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * สำหรับอัปโหลดไฟล์เดียว (รองรับทั้ง PDF และ Image)
     * POST /api/file/upload
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = fileService.uploadFile(file);
        return ResponseEntity.ok(response);
    }

    /**
     * สำหรับอัปโหลดรูปภาพหลายรูปพร้อมกัน
     * POST /api/file/upload-images
     */
    @PostMapping("/upload-images")
    public ResponseEntity<List<String>> uploadImages(@RequestParam("images") List<MultipartFile> images) {
        List<String> imageUrls = fileService.uploadImage(images);
        return ResponseEntity.ok(imageUrls);
    }
}