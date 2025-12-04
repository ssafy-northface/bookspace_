package com.bookspace.domain.user.controller;

import com.bookspace.domain.user.dto.LoginRequestDto;
import com.bookspace.domain.user.dto.SignupRequestDto;
import com.bookspace.domain.user.dto.UserResponseDto;
import com.bookspace.domain.user.dto.UserUpdateRequestDto;
import com.bookspace.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    // 1. 회원가입
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signup(@RequestBody SignupRequestDto dto) {
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
    public ResponseEntity<UserResponseDto> login(@RequestBody LoginRequestDto dto) {
        UserResponseDto response = userService.login(dto);
        return ResponseEntity.ok(response);
    }


    // 3. 회원 정보 조회 (by userId)
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable long userId) {
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


    // =============================================================
    // 6~8. 중복 체크 API (프론트 회원가입 화면용)
    //     - 아이디 / 닉네임 / 이메일 실시간 중복 확인
    //     - 회원가입 UX 개선을 위한 기능
    //     - 가입 시 실제 중복 검사는 signup() 내부에서 다시 수행됨
    // =============================================================


    // 6. 로그인 아이디 중복 체크
    // true = 사용 가능
    @GetMapping("/check/login-id")
    public ResponseEntity<Boolean> checkLoginId(@RequestParam String userLoginId) {
        boolean exists = userService.existsUserByLoginId(userLoginId);
        return ResponseEntity.ok(!exists);
    }

    // 7. 닉네임 중복 체크
    // true = 사용 가능
    @GetMapping("/check/nickname")
    public ResponseEntity<Boolean> checkNickname(@RequestParam String userNickname) {
        boolean exists = userService.existsUserByNickname(userNickname);
        return ResponseEntity.ok(!exists);
    }


    // 8. 이메일 중복 체크
    @GetMapping("/check/email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String userEmail) {
        boolean exists = userService.existsUserByEmail(userEmail);
        return ResponseEntity.ok(!exists);
    }


}
