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

    // 게시글 조회 (by bookId)
    List<PostResponseDto> getPostsByBookId(long bookId);

    // 게시글 조회 (by userId)
    List<PostResponseDto> getPostsByUserId(long userId);

    // 게시글 조회 (by keyword)
    List<PostResponseDto> getPostsByKeyword(String keyword);

}
