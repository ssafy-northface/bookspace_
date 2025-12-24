package com.bookspace.domain.email.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailVerifyCodeRequestDto {
    private String email;
    private String code;
}

