package com.bookspace.domain.user.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserVo {

    private long userId;
    private String userLoginId;
    private String userName;
    private String userNickname;
    private String userPw;
    private LocalDate userBirthDate;
    private String userPhone;
    private String userEmail;
    private LocalDateTime userRegistDate;
    private LocalDateTime deletedAt;
    private String userStatus;
    private String userRole;

}
