package com.bookspace.domain.comment.dto;

import lombok.Data;

@Data
public class CommentRequestDto {

    private long userId;
    private String commentContent;
}
