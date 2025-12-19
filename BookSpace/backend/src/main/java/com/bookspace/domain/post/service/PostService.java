package com.bookspace.domain.post.service;

import java.util.List;

import com.bookspace.domain.post.dto.PostPageResponseDto;
import com.bookspace.domain.post.dto.PostRequestDto;
import com.bookspace.domain.post.dto.PostResponseDto;

public interface PostService {

    // 게시글 등록
    void createPost(PostRequestDto requestDto, long loginUserId);

    // 게시글 전체 조회 &  게시물 검색 (페이지네이션 적용) + 정렬 (sort: latest / comments)
    PostPageResponseDto getAllPosts(int page, int size, String isbn, String sort, Long userId);

    // 게시글 단건 조회
    PostResponseDto getPostById(long postId, Long userId);

    // 게시글 수정
    void updatePost(long postId, PostRequestDto requestDto, long loginUserId);

    // 게시글 삭제
    void deletePost(long postId, Long loginUserId);

    // 조회수 증가
    void increaseViewCount(long postId);

    // 게시글 조회 (by bookId)
    List<PostResponseDto> getPostsByBookId(long bookId);

    // 게시글 조회 (by userId)
    List<PostResponseDto> getPostsByUserId(long userId);

    // 게시글 조회 (by keyword)
    List<PostResponseDto> getPostsByKeyword(String keyword);

    // 유저가 좋아하는 게시글 전체조회
    List<PostResponseDto> getLikedPostsByUserId(Long userId);

    // 특정 책에 대한 게시글 개수 조회
    Integer getPostCountByBookId(long bookId);
}
