package com.bookspace.domain.comment.dao;

import com.bookspace.domain.comment.dto.CommentResponseDto;
import com.bookspace.domain.comment.vo.CommentVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentDao {
    // 1. [C] 댓글 등록
    int insertComment(CommentVo commentVo);

    // 2. [R] 특정 postId의 모든 댓글 조회
    List<CommentResponseDto> selectCommentsByPostId(@Param("postId") long postId);

    // 3. [R] commentId로 단건 조회
    CommentResponseDto selectCommentById(@Param("commentId") long commentId);

    // 4. [R] 특정 userId의 모든 댓글 조회
    List<CommentResponseDto> selectCommentsByUserId(@Param("userId") long userId);

    // 5. [U] - 댓글 수정
    int updateComment(CommentVo commentVo);

    // 6. [D] - 댓글 삭제
    int deleteComment(@Param("commentId") long commentId);
}
