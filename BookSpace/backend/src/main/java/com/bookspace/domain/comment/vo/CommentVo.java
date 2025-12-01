package com.bookspace.domain.comment.vo;

import lombok.Data;

@Data
public class CommentVo {
    private long postId;
    private long userId;
    private String commentContent;
}
