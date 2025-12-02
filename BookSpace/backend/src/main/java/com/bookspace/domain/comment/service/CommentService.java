package com.bookspace.domain.comment.service;

import com.bookspace.domain.comment.dto.CommentRequestDto;
import com.bookspace.domain.comment.dto.CommentResponseDto;

import java.util.List;

public interface CommentService {
    // [C]
    void createComment(long postId, CommentRequestDto commentDto);

    // [R]
    // postId로 댓글 전체 조회
    List<CommentResponseDto> getCommentByPostId (long postId);

    // commentId로 댓글 단건 조회
    CommentResponseDto getCommentByCommentId (long commentId);

    // userId로 댓글 전체 조회
    List<CommentResponseDto> getCommentByUserId (long userId);

    // [U[
    void updateComment(long commentId, CommentRequestDto commentDto);

    // [D]
    void deleteComment(long commentId);
}