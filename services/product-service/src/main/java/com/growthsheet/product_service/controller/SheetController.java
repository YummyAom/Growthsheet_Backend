package com.growthsheet.product_service.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import com.growthsheet.product_service.dto.request.RejectRequest;
import com.growthsheet.product_service.dto.request.ReportSheetRequest;

import com.growthsheet.product_service.dto.request.CreateSheetRequest;
import com.growthsheet.product_service.dto.response.DownloadResponse;
import com.growthsheet.product_service.dto.response.ProductResponseDTO;
import com.growthsheet.product_service.dto.response.SheetCardResponse;
import com.growthsheet.product_service.dto.response.SheetReportResponse;
import com.growthsheet.product_service.dto.response.SheetResponse;
import com.growthsheet.product_service.dto.PageResponse;
import com.growthsheet.product_service.service.FileService;
import com.growthsheet.product_service.service.SheetLikeService;
import com.growthsheet.product_service.service.SheetReportService;
import com.growthsheet.product_service.service.SheetService;

import org.springframework.data.domain.Pageable;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class SheetController {
    private final SheetService sheetService;
    private final FileService fileService;
    private final SheetLikeService sheetLikeService;
    private final SheetReportService sheetReportService;

    public SheetController(
            SheetService sheetService,
            FileService fileService,
            SheetLikeService sheetLikeService,
            SheetReportService sheetReportService) {
        this.sheetService = sheetService;
        this.fileService = fileService;
        this.sheetLikeService = sheetLikeService;
        this.sheetReportService = sheetReportService;
    }

    @GetMapping("/")
    public String hello() {
        return "Hello products";
    }

    @GetMapping("/tags")
    public ResponseEntity<List<String>> getAllTags() {
        return ResponseEntity.ok(sheetService.getAllTags());
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SheetResponse> createSheet(
            @RequestHeader("X-USER-ID") UUID sellerId,
            @Valid @RequestPart("data") CreateSheetRequest req,
            @RequestPart("filePDF") MultipartFile pdfFile,
            @RequestPart("previewImage") List<MultipartFile> previewImages) {

        Map<String, Object> pdf = fileService.uploadFile(pdfFile);
        List<String> images = fileService.uploadImage(previewImages);

        SheetResponse response = sheetService.createSheet(req, sellerId, pdf, images);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/files/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(fileService.uploadFile(file));
    }

    // เรียกดู purchased Order
    @GetMapping("/purchased")
    public PageResponse<SheetCardResponse> getPurchasedSheets(
            @RequestHeader("X-USER-ID") UUID userId,
            Pageable pageable) {

        return sheetService.getPurchasedSheets(userId, pageable);
    }

    @GetMapping("/{id}/open")
    public ResponseEntity<String> openProduct(
            @PathVariable UUID id,
            @RequestHeader("X-USER-ID") UUID userId) {

        String url = sheetService.getSheetFileUrl(id, userId);
        return ResponseEntity.ok(url);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<DownloadResponse> downloadProduct(
            @PathVariable UUID id,
            @RequestHeader("X-USER-ID") UUID userId) {

        return ResponseEntity.ok(
                sheetService.getDownloadInfo(id, userId));
    }

    @GetMapping("{sheetId}/adminDowload")
    public ResponseEntity<DownloadResponse> adminDownload(
            @PathVariable UUID sheetId) {
        return ResponseEntity.ok(sheetService.getDownloadInfoAdmin(sheetId));
    }

    @GetMapping
    public ResponseEntity<Page<SheetCardResponse>> getSheets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(required = false) Boolean isPublished,
            @RequestParam(required = false) List<String> tags) {

        return ResponseEntity.ok(
                sheetService.getSheets(page, size, sort, isPublished, tags));
    }

    @GetMapping("/{sheetId}")
    public ResponseEntity<ProductResponseDTO> getSheetById(
            @PathVariable UUID sheetId) {
        return ResponseEntity.ok(sheetService.getSheet(sheetId));
    }

    @GetMapping("/sellers/{sellerId}/sheets")
    public ResponseEntity<Page<SheetCardResponse>> getSheetPageByUserId(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Boolean isPublished,
            @PathVariable UUID sellerId) {

        return ResponseEntity.ok(
                sheetService.findSheetPageByUserId(sellerId, page, size, isPublished));
    }

    @PostMapping("/{sheetId}/like")
    public ResponseEntity<?> toggleLike(
            @PathVariable UUID sheetId,
            @RequestHeader("X-USER-ID") UUID userId) {

        boolean liked = sheetLikeService.toggleLike(sheetId, userId);

        return ResponseEntity.ok(
                Map.of("liked", liked));
    }

    @GetMapping("/liked")
    public ResponseEntity<Page<SheetCardResponse>> getLikedSheets(
            @RequestHeader("X-USER-ID") UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                sheetLikeService.getLikedSheets(userId, page, size));
    }

    @Value("${internal.service.token}")
    private String internalServiceToken;

    @PatchMapping("/{sheetId}/approve")
    public ResponseEntity<String> approveSheet(
            @RequestHeader("X-INTERNAL-TOKEN") String token,
            @PathVariable UUID sheetId) {

        if (!internalServiceToken.equals(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized");
        }

        sheetService.approveSheet(sheetId);
        return ResponseEntity.ok("อนุมัติชีทเรียบร้อยแล้ว");
    }

    @PatchMapping("/{sheetId}/reject")
    public ResponseEntity<String> rejectSheet(
            @RequestHeader("X-INTERNAL-TOKEN") String token,
            @PathVariable UUID sheetId,
            @RequestBody RejectRequest request) {

        if (!internalServiceToken.equals(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized");
        }

        sheetService.rejectSheet(sheetId, request.getComment());
        return ResponseEntity.ok("ปฏิเสธชีทเรียบร้อยแล้ว");
    }

    /**
     * ดูประวัติการขอ publish sheet ของ seller (รอ admin อนุมัติ)
     * GET /api/products/seller/sheet-applications?status=PENDING&page=0&size=10
     *
     * status: PENDING | APPROVED | REJECTED | null (ทั้งหมด)
     */
    @GetMapping("/seller/sheet-applications")
    public ResponseEntity<Page<SheetCardResponse>> getSheetPublicationHistory(
            @RequestHeader("X-USER-ID") UUID sellerId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                sheetService.getSheetPublicationHistory(sellerId, status, page, size));
    }

    /**
     * User กด report sheet พร้อมเหตุผล
     * POST /api/products/{sheetId}/report
     */
    @PostMapping("/{sheetId}/report")
    public ResponseEntity<SheetReportResponse> reportSheet(
            @PathVariable UUID sheetId,
            @RequestHeader("X-USER-ID") UUID userId,
            @Valid @RequestBody ReportSheetRequest request) {

        SheetReportResponse response = sheetReportService.reportSheet(sheetId, userId, request.getReason());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * ดึง report ทั้งหมด (สำหรับ admin เรียกผ่าน internal token)
     * GET /api/products/reports?status=PENDING&page=0&size=10
     */
    @GetMapping("/reports")
    public ResponseEntity<Page<SheetReportResponse>> getReports(
            @RequestHeader("X-INTERNAL-TOKEN") String token,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (!internalServiceToken.equals(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(sheetReportService.getReports(status, page, size));
    }

    /**
     * ดึง report ทั้งหมดของ sheet นั้น (สำหรับ admin เรียกผ่าน internal token)
     * GET /api/products/{sheetId}/reports?page=0&size=10
     */
    @GetMapping("/{sheetId}/reports")
    public ResponseEntity<Page<SheetReportResponse>> getReportsBySheet(
            @RequestHeader("X-INTERNAL-TOKEN") String token,
            @PathVariable UUID sheetId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (!internalServiceToken.equals(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(sheetReportService.getReportsBySheetId(sheetId, page, size));
    }

    /**
     * Admin review report (อัปเดต status) ผ่าน internal token
     * PATCH /api/products/reports/{reportId}/review
     */
    @PatchMapping("/reports/{reportId}/review")
    public ResponseEntity<SheetReportResponse> reviewReport(
            @RequestHeader("X-INTERNAL-TOKEN") String token,
            @PathVariable UUID reportId,
            @RequestParam String status,
            @RequestParam(required = false) String adminNote,
            @RequestParam(required = false) Boolean suspendSheet,
            @RequestParam UUID adminId) {

        if (!internalServiceToken.equals(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        com.growthsheet.product_service.entity.ReportStatus newStatus =
                com.growthsheet.product_service.entity.ReportStatus.valueOf(status.toUpperCase());

        return ResponseEntity.ok(
                sheetReportService.reviewReport(reportId, adminId, newStatus, adminNote, suspendSheet));
    }

    /**
     * ดึงชีทของ Seller ที่ถูกระงับ (โดนปิด publish แต่ว่าตัวชีทเคยอนุมัติไปแล้ว)
     * GET /api/products/seller/suspended-sheets?page=0&size=10
     */
    @GetMapping("/seller/suspended-sheets")
    public ResponseEntity<Page<SheetCardResponse>> getSuspendedSheets(
            @RequestHeader("X-USER-ID") UUID sellerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(sheetService.getSuspendedSheets(sellerId, page, size));
    }

}
