package com.bookspace.domain.post.dto;

import lombok.Data;

@Data
public class PostRequestDto {
    private long userId;
    private long bookId;
    private String postTitle;
    private String postContent;
}
