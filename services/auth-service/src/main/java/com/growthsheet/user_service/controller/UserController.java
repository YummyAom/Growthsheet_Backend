package com.growthsheet.user_service.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @GetMapping("/")
    public String hello(){
        return "hello user";
    }
    // @PostMapping("/registorSeller")
    // public String registorSeller(
    //      @RequestHeader("X-USER-ID") UUID userId,
    //      @RequestBody registorSellerRequest
    // ) {

    // }
}
