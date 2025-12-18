package com.bookspace.domain.wish.controller;

import java.util.List;

import com.bookspace.domain.wish.dto.WishRequestDto;
import com.bookspace.global.security.userdetails.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.bookspace.domain.wish.dto.WishResponseDto;
import com.bookspace.domain.wish.service.WishService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/wishes")
public class WishController {

    private final WishService wishService;

    /**
     * 현재 로그인한 유저의 userId 가져오기 (JWT 인증 기반)
     */
    private long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            // 보통 SecurityConfig에서 /wishes/** 접근 자체가 막히지만 방어적으로 처리
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 후 이용 가능한 서비스입니다");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUserId();
    }

    /**
     * 1) 찜 하기: POST /wishes
     * - 프론트에서는 isbn만 보내고
     * - userId는 서버에서 현재 로그인 유저 기준으로 세팅
     */
    @PostMapping
    public ResponseEntity<String> addWish(@RequestBody WishRequestDto wishRequestDto) {

        long userId = getCurrentUserId();
        wishRequestDto.setUserId(userId);

        wishService.addWish(wishRequestDto);
        return ResponseEntity.ok("Wish added successfully!");
    }

    /**
     * 2) 찜 해제: DELETE /wishes/{bookId}
     */
    @DeleteMapping("/{bookId}")
    public ResponseEntity<String> removeWish(@PathVariable long bookId) {
        long userId = getCurrentUserId();
        wishService.removeWish(userId, bookId);
        return ResponseEntity.ok("Wish deleted successfully!");
    }

    // 3. 찜한 책 목록 조회: GET /wishes
    // 타인의 찜 목록은 못 보는게 일반적
    // 굳이 url에 userId를 표기할 필요가 X
    @GetMapping
    public ResponseEntity<List<WishResponseDto>> getMyWishes() {
        long userId = getCurrentUserId();
        List<WishResponseDto> wishes = wishService.getWishesByUserId(userId);
        return ResponseEntity.ok(wishes);
    }


    // Controller에서는 필요 X -> bookDto에서 필요
//    // 4. 책별 찜 개수
//    @GetMapping("/count/book/{bookId}")
//    public ResponseEntity<Integer> getWishCountByBook(@PathVariable long bookId) {
//        int count = wishService.getWishCountByBookId(bookId);
//        return ResponseEntity.ok(count);
//    }

//    // 5. user가 찜한 책 개수
//    @GetMapping("/count/me")
//    public ResponseEntity<Integer> getMyWishCount() {
//        long userId = getCurrentUserId();
//        int count = wishService.getWishCountByUserId(userId);
//        return ResponseEntity.ok(count);
//    }
}
