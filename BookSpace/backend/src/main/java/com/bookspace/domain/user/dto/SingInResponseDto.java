package com.bookspace.domain.user.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 로그인 응답 DTO
 * - 서버 -> 클라이언트로 전달되는 JWT 토큰 정보
 */
@Getter
@AllArgsConstructor
public class SingInResponseDto {
    private String accessToken;
    private String refreshToken;
}
