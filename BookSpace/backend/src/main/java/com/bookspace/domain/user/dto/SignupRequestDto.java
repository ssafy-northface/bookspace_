package com.bookspace.domain.user.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDto {

    // user가 회원가입하는데 필요한 정보

    @NotBlank(message = "로그인 ID는 필수입니다.")
    @Size(min = 5, max = 20, message = "로그인 ID는 5자 이상 20자 이하여야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "로그인 ID는 영문, 숫자만 사용할 수 있습니다.")
    private String userLoginId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,20}$",
            message = "비밀번호는 소문자, 숫자, 특수문자를 각각 최소 1자 이상 포함해야 합니다."
    )
    private String userPw;

    @NotBlank(message = "이름은 필수입니다.")
    private String userName;

    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(min = 2, max = 12, message = "닉네임은 2자 이상 12자 이하여야 합니다.")
    private String userNickname;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String userEmail;

    @Pattern(
            regexp = "^$|^01[016789]-\\d{3,4}-\\d{4}$",
            message = "전화번호 형식이 올바르지 않습니다."
    )
    private String userPhone;

    @Past(message = "생년월일은 과거 날짜여야 합니다.")
    private java.time.LocalDate userBirthDate;
}
