package com.bookspace.domain.comment.dto;

import lombok.Data;

@Data
public class CommentRequestDto {

    private Long parentCommentId; // 일반 댓글이면 null
    private String commentContent;
}
