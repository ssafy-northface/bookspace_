package com.bookspace.domain.post.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PostResponseDto {
    private long postId;
    private long userId;
    private long bookId;
    private String postTitle;
    private String postContent;
    private LocalDateTime postDate;
    private int postViewCnt;
    private LocalDateTime postLastModified;
}
