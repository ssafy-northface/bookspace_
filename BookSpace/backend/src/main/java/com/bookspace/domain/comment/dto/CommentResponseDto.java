package com.bookspace.domain.comment.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CommentResponseDto {
    private long commentId;
    private long postId;
    private long userId;
    private String userNickname;
    private Long parentCommentId;
    private String commentContent;
    private LocalDateTime commentDate;
    private LocalDateTime commentLastModified;
    private List<CommentResponseDto> replies = new ArrayList<>();// 대댓글 목록 (depth=1)
}
