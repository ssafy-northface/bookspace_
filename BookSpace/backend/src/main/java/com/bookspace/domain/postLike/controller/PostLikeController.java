package com.bookspace.domain.post_like.controller;

import com.bookspace.domain.post_like.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostLikeController {

    //private final PostLikeService postLikeService;

    // 좋아요 추가 posts/{postId}/like
//    @PostMapping("/{postId}/like")
//    public ResponseEntity<String> addLike(@PathVariable("postId") Long postId,@RequestBody Post) {}
}
