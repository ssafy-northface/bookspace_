package com.bookspace.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserResponseDto {

    // user가 개인정보를 조회하는데 필요한 정보
    private Long userId;
    private String userLoginId;
    private String userName;
    private String userNickname;
    private String userEmail;
    private String userPhone;
    private LocalDate userBirthDate;
    private LocalDateTime userRegistDate;
    private String userStatus;
}
