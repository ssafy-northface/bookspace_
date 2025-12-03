package com.bookspace.domain.post.vo;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PostVo {
    private long postId;
    private long userId;
    private long bookId;
    private String postTitle;
    private String postContent;
    private LocalDateTime postDate;
    private int postViewCnt;
    private LocalDateTime postLastModified;
    private int likeCount;
    private boolean isLiked;
}
