package com.bookspace.domain.user.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserUpdateRequestDto {

    // user가 수정가능한 정보만 포함
    private String userNickname;
    private String userPhone;
    private LocalDate userBirthDate;

}
