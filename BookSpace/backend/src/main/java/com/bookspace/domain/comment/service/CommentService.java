package com.bookspace.domain.comment.service;

import com.bookspace.domain.comment.dto.CommentRequestDto;
import com.bookspace.domain.comment.dto.CommentResponseDto;

public interface CommentService {
    void createComment(long postId, CommentRequestDto commentDto);
}