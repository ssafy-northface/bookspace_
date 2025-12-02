package com.bookspace.domain.postLike.dao;

import com.bookspace.domain.postLike.vo.PostLikeVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PostLikeDao {

    int insertLike(PostLikeVo vo);

    int existsLike(@Param("postId") long postId, @Param("userId") long userId);

    int deleteLike(@Param("postId") long postId, @Param("userId") long userId);

}
