package com.growthsheet.admin_service.controller;

import org.springframework.boot.data.autoconfigure.web.DataWebProperties.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import feign.ResponseMapper;

// @RequestMapping("api/admin")
// public class AdminController {
//     @GetMapping("/products/{status}")
//     public ResponseEntity<Page<?>> getsSheetByStatus(
//         @RequestParam(defaultValue = "0") int page,
//         @RequestParam(defaultValue = "10") int size
 
//     )
// }
