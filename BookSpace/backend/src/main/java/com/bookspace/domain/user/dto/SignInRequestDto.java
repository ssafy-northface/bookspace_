package com.bookspace.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 로그인 요청 DTO
 * - 클라이언트 -> 서버로 전달되는 로그인 정보
 */
@Getter
@Setter
public class SignInRequestDto {

    // user가 로그인하는데 필요한 정보
    private String userLoginId;
    private String userPw;
}
