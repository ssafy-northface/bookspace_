package com.bookspace.domain.post.service;

import java.util.List;

import com.bookspace.domain.post.dto.PostRequestDto;
import com.bookspace.domain.post.dto.PostResponseDto;

public interface PostService {

    // 게시글 등록
    void createPost(PostRequestDto requestDto);

    // 게시글 전체 조회
    List<PostResponseDto> getAllPosts();

    // 게시글 단건 조회
    PostResponseDto getPostById(long postId);

    // 게시글 수정
    void updatePost(long postId, PostRequestDto requestDto);

    // 게시글 삭제
    void deletePost(long postId);

    // 조회수 증가
    void increaseViewCount(long postId);
}
