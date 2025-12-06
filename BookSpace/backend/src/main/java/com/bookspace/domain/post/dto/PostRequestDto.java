package com.bookspace.domain.post.dto;

import lombok.Data;

@Data
public class PostRequestDto {
    private long userId;
    private String isbn; // 유저가 DB내 없는 책으로 게시글을 사용할 경우
    private String postTitle;
    private String postContent;
}
