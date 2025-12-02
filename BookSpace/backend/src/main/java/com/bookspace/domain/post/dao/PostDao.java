package com.bookspace.domain.post.dao;

import java.util.List;
import java.util.Map;

import com.bookspace.domain.post.dto.PostResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.bookspace.domain.post.vo.PostVo;

@Mapper
public interface PostDao {

    // 1. 게시글 등록
    int insertPost(PostVo postVo);

    // 2. 게시글 전체 조회
    List<PostVo> selectAllPosts();

    // 3. 게시글 단건 조회
    PostVo selectPostById(@Param("postId") long postId);

    // 4. 게시글 수정
    int updatePost(PostVo postVo);

    // 5. 게시글 삭제
    int deletePost(@Param("postId") long postId);

    // 6. 조회수 증가
    int increaseViewCount(@Param("postId") long postId);

    // 7. 게시글 조회 (by bookId)
    List<PostVo> selectPostsByBookId(@Param("bookId") long bookId);

    // 8. 게시글 조회 (by userId)
    List<PostVo> selectPostsByUserId(@Param("userId") long userId);

    // 9. 게시글 조회 (by keyword)
    List<PostVo> selectPostsByKeyword(@Param("keyword") String keyword);

    //10. 게시글 존재 여부
    boolean existsById(@Param("postId") long postId);



}
