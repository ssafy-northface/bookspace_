package com.bookspace.domain.user.controller;

import com.bookspace.domain.user.dto.SignInRequestDto;
import com.bookspace.domain.user.dto.SignupRequestDto;
import com.bookspace.domain.user.dto.SingInResponseDto;
import com.bookspace.domain.user.dto.UserResponseDto;
import com.bookspace.domain.user.service.UserService;
import com.bookspace.global.security.jwt.JwtTokenProvider;
import com.bookspace.global.security.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    /**
     * 로그인 API
     * - userLoginId, userPw 인증 성공 -> jwt 발급
     */
    @PostMapping("/login")
    public SingInResponseDto login(@RequestBody SignInRequestDto dto){
        // 1) spring security 인증
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUserLoginId(), dto.getUserPw())
        );

        // 2) 인증 성공 -> jwt 발급
        String accessToken = jwtTokenProvider.createAccessToken(dto.getUserLoginId());
        String refreshToken = jwtTokenProvider.createRefreshToken(dto.getUserLoginId());

        // 3) 토큰 응답
        return new SingInResponseDto(accessToken,refreshToken);

    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long loginUserId = userDetails.getUserId();

        UserResponseDto dto = userService.getMyInfo(loginUserId);

        return ResponseEntity.ok(dto);
    }

}
