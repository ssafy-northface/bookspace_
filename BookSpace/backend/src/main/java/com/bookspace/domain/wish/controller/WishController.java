package com.bookspace.domain.wish.controller;

import java.util.List;

import com.bookspace.domain.wish.dto.WishRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bookspace.domain.wish.dto.WishResponseDto;
import com.bookspace.domain.wish.service.WishService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/wish")
public class WishController {

    private final WishService wishService;

    // TODO
    // 로그인 구현 후 수정 (현재는 userId = 1로 hard-coding)
    private long getCurrentUserId() {
        // e.g. from SecurityContextHolder / session / JWT etc.
        return 1L;
    }

    // 1. 찜 하기: POST /wish
    @PostMapping
    public ResponseEntity<String> addWish(@RequestBody WishRequestDto wishRequestDto) {
        wishService.addWish(wishRequestDto);
        return ResponseEntity.ok("Wish added successfully!");
    }

    // 2. 찜 해제: DELETE /wishes?bookId=10
    @DeleteMapping
    public ResponseEntity<Void> removeWish(@RequestParam long bookId) {
        long userId = getCurrentUserId();
        wishService.removeWish(userId, bookId);
        return ResponseEntity.ok().build();
    }

    // 3. 찜한 책 목록 조회: GET /wishes/books
    // 타인의 찜 목록은 못 보는게 일반적
    // 굳이 url에 userId를 표기할 필요가 X
    @GetMapping("/books")
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
