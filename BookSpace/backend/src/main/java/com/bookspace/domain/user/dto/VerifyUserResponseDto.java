package com.bookspace.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerifyUserResponseDto {
    private boolean verified;
    private String resetLink; // 재설정 링크 (가입 시 이메일 인증 완료된 경우)
    private boolean isLinkSent; // 링크 발송 여부 (true: 링크, false: 코드)
}

