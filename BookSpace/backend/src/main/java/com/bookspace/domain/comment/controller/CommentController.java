package com.bookspace.domain.comment.controller;

import com.bookspace.domain.comment.dto.CommentRequestDto;
import com.bookspace.domain.comment.dto.CommentResponseDto;
import com.bookspace.domain.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // [C] 댓글 생성 (Create)
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<String> createComment(
            @PathVariable("postId") long postId,
            @RequestBody CommentRequestDto commentDto) {
        commentService.createComment(postId, commentDto);
        return ResponseEntity.ok("Comment created successfully");
    }

    // [R] - postId 특정 게시글의 전체 댓글 조회: 게시글 상세보기 시 모든 댓글 조회
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponseDto>> getCommentsByPostId(@PathVariable long postId){
        return ResponseEntity.ok(commentService.getCommentByPostId(postId));
    }

    // [R] - commentId 특정 댓글 단건 조회 : 댓글 수정 시
    @GetMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> getCommentById(@PathVariable long commentId){
        return ResponseEntity.ok(commentService.getCommentByCommentId(commentId));
    }

    // [R] - userId 특정 유저가 작성한 댓글 전체 조회 : 마이페이지에서 유저가 자신이 남긴 모든 댓글 목록 불러오기
    @GetMapping ("comments/user/{userId}")
    public ResponseEntity<List<CommentResponseDto>> getCommentsByUserId(@PathVariable long userId){
        return ResponseEntity.ok(commentService.getCommentByUserId(userId));
    }

    // [U] - 댓글 수정
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(@PathVariable long commentId, @RequestBody CommentRequestDto commentDto){
        commentService.updateComment(commentId, commentDto);
        // 수정 후 반환
        return ResponseEntity.ok(commentService.getCommentByCommentId(commentId));
    }

    // [D] - 댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable long commentId){
        commentService.deleteComment(commentId);
        return ResponseEntity.ok("Comment deleted successfully");
    }


}
