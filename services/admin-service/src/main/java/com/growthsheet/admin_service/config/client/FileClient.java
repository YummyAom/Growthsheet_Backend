package com.growthsheet.admin_service.config.client;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;



@FeignClient(
        name = "file-service",
        url = "${GATEWAY_SERVICE_URL}")
public interface FileClient {

    @PostMapping(value = "/file/upload-slip", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Map<String, Object> uploadSlip(@RequestPart("file") MultipartFile file);
}