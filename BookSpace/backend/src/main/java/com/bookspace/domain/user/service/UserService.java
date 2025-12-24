package com.bookspace.domain.user.service;

import com.bookspace.domain.user.dto.SignInRequestDto;
import com.bookspace.domain.user.dto.SignupRequestDto;
import com.bookspace.domain.user.dto.UserResponseDto;
import com.bookspace.domain.user.dto.UserUpdateRequestDto;

public interface UserService {

    // 1. 회원가입
    UserResponseDto signup(SignupRequestDto dto);

    // 2. 로그인
    UserResponseDto login(SignInRequestDto dto);

    // 3. 회원 정보 조회 (by userId)
    UserResponseDto getUserById(long userId);

    // 4. 회원 정보 수정
    UserResponseDto updateUser(long userId, UserUpdateRequestDto dto);

    // 5. 회원 탈퇴 (soft delete)
    void softDeleteUser(long userId);

    // 6. 회원 영구 삭제 (hard delete)
    // userId를 기반으로 한 단건 삭제 => admin 권한 강제 삭제용으로 사용해도 됨
    void hardDeleteUser(long userId);

    // 7. 로그인 아이디 중복 체크
    boolean existsUserByLoginId(String userLoginId);

    // 8. 닉네임 중복 체크
    boolean existsUserByNickname(String userNickname);

    // 9. 이메일 중복 체크
    boolean existsUserByEmail(String userEmail);

    // 10. inactive 14일 경과 유저 일괄 영구 삭제 (CleanupScheduler 전용)
    int deleteExpiredInactiveUsers();

    // 11. 로그인한 사용자의 내 정보 조회
    UserResponseDto getMyInfo(Long userId);

    // 12. 비밀번호 찾기 - 본인 확인 (아이디 + 이메일 일치 여부)
    boolean verifyUser(String userLoginId, String userEmail);

    // 13. 비밀번호 재설정
    void resetPassword(String userLoginId, String userEmail, String newPassword);
}
