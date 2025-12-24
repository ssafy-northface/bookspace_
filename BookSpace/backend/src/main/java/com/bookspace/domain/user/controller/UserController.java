package com.bookspace.domain.user.controller;

import com.bookspace.domain.user.dto.SignInRequestDto;
import com.bookspace.domain.user.dto.SignupRequestDto;
import com.bookspace.domain.user.dto.UserResponseDto;
import com.bookspace.domain.user.dto.UserUpdateRequestDto;
import com.bookspace.domain.user.dto.VerifyUserRequestDto;
import com.bookspace.domain.user.dto.VerifyUserResponseDto;
import com.bookspace.domain.user.dto.ResetPasswordRequestDto;
import com.bookspace.domain.user.service.UserService;
import com.bookspace.global.security.userdetails.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    // 1. 회원가입
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signup(@Valid @RequestBody SignupRequestDto dto) {
        UserResponseDto response = userService.signup(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. 로그인
    // =============================================================
    // Spring Security 적용 후 수정 필요
    // -------------------------------------------------------------
    // 향후 Security/JWT 도입 시
    // 1) /login API는 AuthenticationManager 기반 인증 절차로 변경됨
    // 2) 로그인 성공 시 UserResponseDto 대신 JWT 토큰을 반환하게 됨
    // 3) 사용자 정보 조회는 SecurityContextHolder에 저장된 인증 정보 사용
    // 4) 인증/인가 로직(Service.login 등)은 모두 제거 또는 대체 예정
    // =============================================================
    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> login(@RequestBody SignInRequestDto dto) {
        UserResponseDto response = userService.login(dto);
        return ResponseEntity.ok(response);
    }


    // 3. 회원 정보 조회 (by userId)
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMe(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new AccessDeniedException("Unauthorized");
        }

        long userId = userDetails.getUserId();
        UserResponseDto response = userService.getUserById(userId);
        return ResponseEntity.ok(response);
    }


    // 4. 회원 정보 수정
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable long userId,
            @RequestBody UserUpdateRequestDto dto) {
        UserResponseDto response = userService.updateUser(userId, dto);
        return ResponseEntity.ok(response);
    }


    // 5. 회원 탈퇴 (soft delete)
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> softDeleteUser(@PathVariable long userId) {
        userService.softDeleteUser(userId);
        // 삭제는 보통 204 NO_CONTENT
        return ResponseEntity.noContent().build();
    }


    // 6. 회원 영구 삭제 (hard delete) - 단건 삭제 / 관리자 권한
    @DeleteMapping("/{userId}/hard")
    public ResponseEntity<Void> hardDeleteUser(@PathVariable long userId) {
        userService.hardDeleteUser(userId);
        // 성공 시 204 No Content
        return ResponseEntity.noContent().build();
    }

    // 7~9 통합
    // 중복 체크 통합 API (loginId, nickname, email)
//    /users/check?type=loginId&value=user123
//    /users/check?type=nickname&value=happyday
//    /users/check?type=email&value=test@example.com
    @GetMapping("/check")
    public ResponseEntity<Boolean> checkDuplicate(
            @RequestParam String type, @RequestParam String value) {

        // switch expression 문법 사용
        boolean exists = switch (type.toLowerCase()) {
            case "loginid" -> userService.existsUserByLoginId(value);
            case "nickname" -> userService.existsUserByNickname(value);
            case "email" -> {
                // 이메일 형식 간단 검증 (안전장치)
                if (!value.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                    throw new IllegalArgumentException("Invalid email format: " + value);
                }
                // yield : switch expression 안에서 값을 반환(return) 하는 최신 키워드
                yield userService.existsUserByEmail(value);
            }
            // loginid, nickname, email이 아닌 type이 들어왔을때 발생 메시지
            default -> throw new IllegalArgumentException("Invalid type: " + type);
        };

        // true = 사용 가능(중복 아님)
        // false = 이미 존재
        return ResponseEntity.ok(!exists);
    }



    // =============================================================
    // 7~9. 중복 체크 API (프론트 회원가입 화면용)
    //     - 아이디 / 닉네임 / 이메일 실시간 중복 확인
    //     - 회원가입 UX 개선을 위한 기능
    //     - 가입 시 실제 중복 검사는 signup() 내부에서 다시 수행됨
    // =============================================================


//    // 7. 로그인 아이디 중복 체크
//    // true = 사용 가능
//    @GetMapping("/check/login-id")
//    public ResponseEntity<Boolean> checkLoginId(@RequestParam String userLoginId) {
//        boolean exists = userService.existsUserByLoginId(userLoginId);
//        return ResponseEntity.ok(!exists);
//    } ,.ㅏ
//
//    // 8. 닉네임 중복 체크
//    // true = 사용 가능
//    @GetMapping("/check/nickname")
//    public ResponseEntity<Boolean> checkNickname(@RequestParam String userNickname) {
//        boolean exists = userService.existsUserByNickname(userNickname);
//        return ResponseEntity.ok(!exists);
//    }
//
//
//    // 9. 이메일 중복 체크
//    @GetMapping("/check/email")
//    public ResponseEntity<Boolean> checkEmail(@RequestParam String userEmail) {
//        boolean exists = userService.existsUserByEmail(userEmail);
//        return ResponseEntity.ok(!exists);
//    }

    // 10. 비밀번호 찾기 - 본인 확인
    @PostMapping("/verify")
    public ResponseEntity<VerifyUserResponseDto> verifyUser(@RequestBody VerifyUserRequestDto dto) {
        boolean verified = userService.verifyUser(dto.getUserLoginId(), dto.getUserEmail());
        return ResponseEntity.ok(new VerifyUserResponseDto(verified));
    }

    // 11. 비밀번호 재설정
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequestDto dto) {
        userService.resetPassword(dto.getUserLoginId(), dto.getUserEmail(), dto.getNewPassword());
        return ResponseEntity.ok().build();
    }

}
