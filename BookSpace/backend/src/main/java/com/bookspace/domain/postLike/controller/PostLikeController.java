package com.bookspace.domain.postLike.controller;

import com.bookspace.domain.postLike.dto.PostLikeRequestDto;
import com.bookspace.domain.postLike.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostLikeController {

    private final PostLikeService postLikeService;

    // 좋아요 추가
    @PostMapping("/{postId}/like")
    public ResponseEntity<String> addLike(@PathVariable Long postId) {
        // TODO 로그인 구현 후 수정
        long userId = 1;
        postLikeService.addLike(postId, userId);
        return ResponseEntity.ok("Like added successfully");
    }

    // 좋아요 취소
    @DeleteMapping ("/{postId}/like")
    public ResponseEntity<String> removeLike(@PathVariable("postId") Long postId) {
        // TODO 로그인 구현 후 수정
        long userId = 1;
        postLikeService.removeLike(postId, userId);
        return ResponseEntity.ok("Like deleted successfully");
    }
}

