package com.bookspace.domain.user.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDto {

    // user가 회원가입하는데 필요한 정보
    private String userLoginId;
    private String userPw;
    private String userName;
    private String userNickname;
    private String userEmail;
    private String userPhone;
    private java.time.LocalDate userBirthDate;
}
