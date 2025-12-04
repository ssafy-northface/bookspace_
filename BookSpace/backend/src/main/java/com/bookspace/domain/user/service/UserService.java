package com.bookspace.domain.user.service;

import com.bookspace.domain.user.dto.LoginRequestDto;
import com.bookspace.domain.user.dto.SignupRequestDto;
import com.bookspace.domain.user.dto.UserResponseDto;
import com.bookspace.domain.user.dto.UserUpdateRequestDto;

public interface UserService {

    // 1. 회원가입
    UserResponseDto signup(SignupRequestDto dto);

    // 2. 로그인
    UserResponseDto login(LoginRequestDto dto);

    // 3. 회원 정보 조회 (by userId)
    UserResponseDto getUserById(long userId);

    // 4. 회원 정보 수정
    UserResponseDto updateUser(long userId, UserUpdateRequestDto dto);

    // 5. 회원 탈퇴 (soft delete)
    void softDeleteUser(long userId);

    // 6. 회원 영구 삭제 (hard delete)
    // cleanupScheduler에서 사용할 예정
    void hardDeleteUser(long userId);

    // 7. 로그인 아이디 중복 체크
    boolean existsUserByLoginId(String userLoginId);

    // 8. 닉네임 중복 체크
    boolean existsUserByNickname(String userNickname);

    // 9. 이메일 중복 체크
    boolean existsUserByEmail(String userEmail);

}
