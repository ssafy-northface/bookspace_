package com.bookspace.domain.email.controller;

import com.bookspace.domain.email.dto.EmailVerificationRequestDto;
import com.bookspace.domain.email.dto.EmailVerificationResponseDto;
import com.bookspace.domain.email.dto.EmailVerifyCodeRequestDto;
import com.bookspace.domain.email.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    /**
     * 인증 코드 발송
     * POST /email/send-code
     */
    @PostMapping("/send-code")
    public ResponseEntity<EmailVerificationResponseDto> sendVerificationCode(
            @RequestBody EmailVerificationRequestDto dto) {
        try {
            if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new EmailVerificationResponseDto(
                        false,
                        "이메일 주소를 입력해주세요."
                ));
            }
            emailVerificationService.sendVerificationCode(dto.getEmail().trim());
            return ResponseEntity.ok(new EmailVerificationResponseDto(
                    true,
                    "인증 코드가 이메일로 발송되었습니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new EmailVerificationResponseDto(
                    false,
                    e.getMessage() != null ? e.getMessage() : "이메일 발송에 실패했습니다. 이메일 주소를 확인해주세요."
            ));
        }
    }

    /**
     * 인증 코드 검증
     * POST /email/verify-code
     */
    @PostMapping("/verify-code")
    public ResponseEntity<EmailVerificationResponseDto> verifyCode(
            @RequestBody EmailVerifyCodeRequestDto dto) {
        boolean verified = emailVerificationService.verifyCode(dto.getEmail(), dto.getCode());

        if (verified) {
            return ResponseEntity.ok(new EmailVerificationResponseDto(
                    true,
                    "이메일 인증이 완료되었습니다."
            ));
        } else {
            return ResponseEntity.badRequest().body(new EmailVerificationResponseDto(
                    false,
                    "인증 코드가 일치하지 않거나 만료되었습니다."
            ));
        }
    }

    /**
     * 이메일 인증 상태 확인
     * GET /email/check?email=xxx
     */
    @GetMapping("/check")
    public ResponseEntity<EmailVerificationResponseDto> checkVerification(
            @RequestParam String email) {
        boolean verified = emailVerificationService.isEmailVerified(email);

        return ResponseEntity.ok(new EmailVerificationResponseDto(
                verified,
                verified ? "인증된 이메일입니다." : "인증되지 않은 이메일입니다."
        ));
    }
}

