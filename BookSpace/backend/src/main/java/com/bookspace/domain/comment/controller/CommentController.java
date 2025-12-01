package com.bookspace.domain.comment.controller;

import com.bookspace.domain.comment.dto.CommentRequestDto;
import com.bookspace.domain.comment.service.CommentService;
import com.bookspace.domain.comment.vo.CommentVo;
import com.bookspace.domain.common.validation.ValidatePostExists;
import com.bookspace.domain.common.validation.ValidateUserExists;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 1. 댓글 생성 (Create)
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<String> createComment(
            @PathVariable("postId") long postId,
            @RequestBody CommentRequestDto commentDto) {
        commentService.createComment(postId, commentDto);
        return ResponseEntity.ok("Comment created successfully");
    }
}
