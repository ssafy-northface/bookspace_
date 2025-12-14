package com.bookspace.domain.postLike.service;

public interface PostLikeService {
    void addLike(long postId, long userId);

    void removeLike(long postId, long userId);
}
