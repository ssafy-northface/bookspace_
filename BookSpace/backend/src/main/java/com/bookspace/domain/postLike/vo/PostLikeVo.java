package com.bookspace.domain.postLike.vo;

import lombok.Data;

@Data
public class PostLikeVo {
    private long likeId;
    private long postId;
    private long userId;
}
