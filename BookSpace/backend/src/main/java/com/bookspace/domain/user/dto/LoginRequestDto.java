package com.bookspace.domain.user.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {

    // user가 로그인하는데 필요한 정보
    private String userLoginId;
    private String userPw;
}
