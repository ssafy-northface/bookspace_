package com.bookspace.domain.user.controller;

import com.bookspace.domain.user.dto.SignInRequestDto;
import com.bookspace.domain.user.dto.SignupRequestDto;
import com.bookspace.domain.user.dto.SingInResponseDto;
import com.bookspace.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

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
}
