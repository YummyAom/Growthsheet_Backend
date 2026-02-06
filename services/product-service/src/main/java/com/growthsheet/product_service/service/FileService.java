package com.growthsheet.product_service.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;


@Service
public class FileService {

    @Autowired
    private Cloudinary cloudinary;

    public Map<String, Object> uploadFile(MultipartFile file) {
        // 1. ตรวจสอบเบื้องต้นว่ามีไฟล์ส่งมาจริงหรือไม่
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty or missing");
        }

        String contentType = file.getContentType();
        Map<String, Object> result = new HashMap<>();

        int pageCount = 0;
        String fileType;

        try {
            // 2. แยกแยะประเภทไฟล์และประมวลผล
            if (isPdf(contentType)) {
                pageCount = getPdfPageCount(file);
                fileType = "PDF";
            } else if (isImage(contentType)) {
                fileType = "IMAGE";
            } else {
                // แจ้งเตือนกลับไปที่ Postman กรณีเลือกไฟล์ผิดประเภท
                throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                        "Only PDF and Image files are supported");
            }

            Map uploadResult = uploadToCloudinary(file);

            // 4. รวบรวมข้อมูลเพื่อส่งให้ Controller นำไปลง Database
            result.put("url", uploadResult.get("secure_url"));
            result.put("pageCount", pageCount);
            result.put("fileType", fileType);
            result.put("fileName", file.getOriginalFilename());

            return result;

        } catch (IOException e) {
            // แจ้งเตือนกรณีเกิดปัญหาทางเทคนิคในการอ่านไฟล์หรืออัปโหลด
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "File processing failed: " + e.getMessage());
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
        // ใช้ Apache PDFBox นับจำนวนหน้า
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            return document.getNumberOfPages();
        }
    }

    @SuppressWarnings("unchecked") // บอก Java ว่าเราตรวจสอบความปลอดภัยของประเภทข้อมูลแล้ว
    private Map<String, Object> uploadToCloudinary(MultipartFile file) throws IOException {
        // 1. กำหนด Parameter สำหรับอัปโหลด
        Map<String, Object> uploadParams = ObjectUtils.asMap(
                "folder", "growthsheet_assets",
                "resource_type", "auto",
                "quality", "auto",
                "fetch_format", "auto");

        // 2. ทำการ Cast ผลลัพธ์จาก Cloudinary ให้เป็น Map<String, Object>
        // uploader().upload() คืนค่าเป็น Map เฉยๆ (Raw Type) จึงต้องระบุประเภทใหม่
        return (Map<String, Object>) cloudinary.uploader().upload(file.getBytes(), uploadParams);
    }
}