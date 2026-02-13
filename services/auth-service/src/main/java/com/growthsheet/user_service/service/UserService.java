package com.growthsheet.user_service.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.growthsheet.user_service.dto.requests.RegistorSellerRequest;
import com.growthsheet.user_service.entity.SellerDetail;
import com.growthsheet.user_service.entity.User;
import com.growthsheet.user_service.entity.UserRole;
import com.growthsheet.user_service.respository.SellerDetailRepository;
import com.growthsheet.user_service.respository.UserRepository;

@Service
public class UserService {

    private final SellerDetailRepository sellerDetailRepository;
    private final UserRepository userRepository;
    private final FileService fileService;
    private final TokenService tokenService;

    // ✅ เขียน constructor เอง
    public UserService(SellerDetailRepository sellerDetailRepository,
            UserRepository userRepository,
            FileService fileService,
            TokenService tokenService
        ) {
        this.sellerDetailRepository = sellerDetailRepository;
        this.userRepository = userRepository;
        this.fileService = fileService;
        this.tokenService = tokenService;
    }

    @Transactional
    public String createSeller(
            UUID userId,
            RegistorSellerRequest request,
            MultipartFile studentCardImage,
            MultipartFile selfieWithCardImage) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("ไม่พบข้อมูลผู้ใช้งาน"));

        if (sellerDetailRepository.existsById(userId)) {
            return "คุณได้ทำการส่งข้อมูลการสมัครผู้ขายไปแล้ว";
        }

        String studentCardUpload = fileService.uploadImage(studentCardImage);
        String selfieUpload = fileService.uploadImage(selfieWithCardImage);

        SellerDetail sellerDetail = new SellerDetail();

        sellerDetail.setUser(user);
        sellerDetail.setNickname(request.nickname());
        sellerDetail.setFullName(request.fullName());
        sellerDetail.setUniversity(request.university());
        sellerDetail.setStudentId(request.studentId());
        sellerDetail.setPhone(request.phone());
        sellerDetail.setEmail(request.email());
        sellerDetail.setStudentCardImage(studentCardUpload);
        sellerDetail.setSelfieWithCardImage(selfieUpload);
        sellerDetail.setBankName(request.bankName());
        sellerDetail.setBankAccountNumber(request.bankAccountNumber());
        sellerDetail.setBankAccountName(request.bankAccountName());
        sellerDetail.setStatus("pending");

        sellerDetailRepository.save(sellerDetail);

        return "ส่งข้อมูลการสมัครผู้ขายเรียบร้อยแล้ว กรุณารอ Admin ตรวจสอบ";
    }

    @Transactional
    public String approveSeller(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("ไม่พบผู้ใช้งาน"));

        SellerDetail sellerDetail = sellerDetailRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("ไม่พบข้อมูลผู้ขาย"));

        sellerDetail.setStatus("approved");

        user.setRole(UserRole.SELLER); 

        sellerDetailRepository.save(sellerDetail);
        userRepository.save(user);

        tokenService.deleteRefreshToken(userId);

        return "อนุมัติผู้ขายเรียบร้อยแล้ว";
    }
}
