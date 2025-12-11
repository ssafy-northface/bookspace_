package com.bookspace.global.security.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.security.auth.Subject;
import java.security.Key;
import java.util.Date;

/**
 * JWT를 생성하고 검증
 * - 인증(auth) 과정에서 토큰 생성
 * - 인가(authorization) 과정에서는 토큰 안의 정보를 사용해 권한 판단
 * - JWT subject = userLoginId
  */

@Component
public class JwtTokenProvider {

    private final Key key;

    // 만료 시간
    private final long accessTokenExpireMs = 1000L * 60 * 60;           // 1시간
    private final long refreshTokenExpireMs = 1000L * 60 * 60 * 24 * 14; // 14일

    public JwtTokenProvider(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Access Token 생성
    public String createAccessToken(String userLoginId) {
        return createToken(userLoginId, accessTokenExpireMs);
    }

    // Refresh Token 생성
    public String createRefreshToken(String userLoginId) {
        return createToken(userLoginId, refreshTokenExpireMs);
    }

    // 토큰 생성 로직
    private String createToken(String subject, long expireMs) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expireMs);

        return Jwts.builder()
                .subject(subject) // userLginId
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();

    }

    // JWT에서 정보 추출
    public String getUserId(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey)key)
                .build()
                .parseSignedClaims(token)
                .getPayload().getSubject(); //userLoginUd 반환
    }

    // JWT 토큰 유효성 검증
    public boolean validateToken(String token) {
        try{
            Jwts.parser()
                    .verifyWith((SecretKey)key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        }catch (JwtException e){
            return false;
        }
    }

}
