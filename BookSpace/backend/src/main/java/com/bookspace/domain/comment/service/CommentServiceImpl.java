package com.bookspace.domain.comment.service;

import com.bookspace.domain.comment.dao.CommentDao;
import com.bookspace.domain.comment.dto.CommentRequestDto;
import com.bookspace.domain.comment.dto.CommentResponseDto;
import com.bookspace.domain.comment.vo.CommentVo;
import com.bookspace.domain.common.validation.ValidatePostExists;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentDao commentDao;

    @Override
    @ValidatePostExists(postId = "#postId")
    // @ValidateUserExists(userId = "commentDto.userId")
    public void createComment(long postId, CommentRequestDto commentDto) {
        CommentVo commentVo = new CommentVo();
        commentVo.setPostId(postId);
        commentVo.setUserId(commentDto.getUserId());
        commentVo.setCommentContent(commentDto.getCommentContent());

        int inserted = commentDao.insertComment(commentVo);

        if(inserted != 1) {
            throw new RuntimeException("Failed to insert comment");
        }


    }
}