package com.bookspace.domain.comment.dao;

import com.bookspace.domain.comment.vo.CommentVo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentDao {
    // 1. 댓글 등록
    int insertComment(CommentVo commentVo);

}
