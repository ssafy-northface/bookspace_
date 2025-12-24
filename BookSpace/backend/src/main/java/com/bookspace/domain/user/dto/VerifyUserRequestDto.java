package com.bookspace.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyUserRequestDto {
    private String userLoginId;
    private String userEmail;
}

