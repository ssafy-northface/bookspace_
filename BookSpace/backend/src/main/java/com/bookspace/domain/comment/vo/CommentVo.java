package com.bookspace.domain.comment.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentVo {
    private long commentId;
    private long postId;
    private long userId;
    private Long parentCommentId;
    private String commentContent;
    private LocalDateTime commentDate;
    private LocalDateTime commentLastModified;
}
