package com.growthsheet.product_service.dto.response;

public class DownloadResponse {

    private String fileUrl;
    private String sheetName;

    public DownloadResponse(String fileUrl, String sheetName) {
        this.fileUrl = fileUrl;
        this.sheetName = sheetName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getSheetName() {
        return sheetName;
    }
}