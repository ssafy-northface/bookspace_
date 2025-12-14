package com.bookspace.domain.postLike.controller;

import com.bookspace.domain.postLike.service.PostLikeService;
import com.bookspace.global.security.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostLikeController {

    private final PostLikeService postLikeService;

    // 좋아요 추가
    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> addLike(@PathVariable Long postId, @AuthenticationPrincipal CustomUserDetails user) {
       if(user==null){
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
       }

        postLikeService.addLike(postId, user.getUserId());
        return ResponseEntity.noContent().build();
    }

    // 좋아요 취소
    @DeleteMapping ("/{postId}/like")
    public ResponseEntity<String> removeLike(@PathVariable Long postId, @AuthenticationPrincipal CustomUserDetails user) {
        if(user==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        postLikeService.removeLike(postId, user.getUserId());
        return ResponseEntity.ok("Like deleted successfully");
    }
}

