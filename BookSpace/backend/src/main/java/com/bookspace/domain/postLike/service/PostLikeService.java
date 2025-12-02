package com.bookspace.domain.postLike.service;

import com.bookspace.domain.postLike.dto.PostLikeRequestDto;

public interface PostLikeService {
    void addLike(long postId, long userId);

    void removeLike(long postId, long userId);
}
