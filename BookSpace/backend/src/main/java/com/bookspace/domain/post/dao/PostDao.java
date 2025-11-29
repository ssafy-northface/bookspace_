package com.bookspace.domain.post.dao;

import java.util.List;

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
    PostVo selectPostById(long postId);


    // 4. 게시글 수정
    int updatePost(PostVo postVo);

    // 5. 게시글 삭제
    int deletePost(@Param("postId") long postId);

    // 6. 조회수 증가
    int increaseViewCount(@Param("postId") long postId);
}
