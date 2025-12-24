package com.bookspace.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordByTokenRequestDto {
    private String token;
    private String newPassword;
}

