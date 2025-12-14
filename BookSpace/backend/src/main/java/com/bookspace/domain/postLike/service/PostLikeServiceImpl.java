package com.bookspace.domain.postLike.service;

import com.bookspace.domain.postLike.dao.PostLikeDao;
import com.bookspace.domain.postLike.vo.PostLikeVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostLikeServiceImpl implements PostLikeService {

    private final PostLikeDao postLikeDao;


    @Override
    @Transactional
    public void addLike(long postId, long userId) {
        // 1) 좋아요 중복 체크
        int exists = postLikeDao.existsLike(postId, userId);
        if(exists>0){
            throw new IllegalArgumentException("User already liked the post: "+ userId);
        }

        // 2) 좋아요 추가
        PostLikeVo postLikeVo = new PostLikeVo();
        postLikeVo.setPostId(postId);
        postLikeVo.setUserId(userId);

        int inserted = postLikeDao.insertLike(postLikeVo);
        if(inserted!=1){
            throw new RuntimeException("PostLike inserted successfully");
        }

    }

    // 3) 좋아요 삭제
    @Override
    public void removeLike(long postId,  long userId) {
        // 1) 좋아요 중복 체크
        int exists = postLikeDao.existsLike(postId, userId);
        if(exists == 0){
            throw new IllegalArgumentException("User already liked the post: "+userId);
        }
        int deleted = postLikeDao.deleteLike(postId, userId);
        if(deleted!=1){
            throw new RuntimeException("PostLike deleted successfully");
        }

    }

}

