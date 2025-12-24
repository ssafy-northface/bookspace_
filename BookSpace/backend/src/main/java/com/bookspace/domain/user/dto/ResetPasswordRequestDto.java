package com.bookspace.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequestDto {
    private String userLoginId;
    private String userEmail;
    private String newPassword;
}

