package com.bookspace.global.security.jwt;

/**
 * 요청에 포함된 jwt를 검사하고,
 * 인증 정보(UserDetails)를 SecurityContextHolder에 저장하는 필터
 * subject = userLoginId 기반으로 인증처리
 */

import com.bookspace.global.security.userdetails.CustomUserDetails;
import com.bookspace.global.security.userdetails.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customDetailService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. authorization 헤더에서 토큰 추출
        String token = resolveToken(request);

        // 2. 토큰 유효성 검사
        if(token != null && jwtTokenProvider.validateToken(token)) {

            // 3. jwt에서 userLoginId 추출
            String userLoginId = jwtTokenProvider.getUserId(token);

            // 4. DB에서 UserDetails 조회
            CustomUserDetails userDetails = (CustomUserDetails)customDetailService.loadUserByUsername(userLoginId);

            // 5. securityContextHolder에 인증 객체 생성 & 정보 저장
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

        }

        // 6. 다음 필터로 넘김
        filterChain.doFilter(request, response);
    }

    // Authorization 헤더에서 Bearer Token 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if(bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}