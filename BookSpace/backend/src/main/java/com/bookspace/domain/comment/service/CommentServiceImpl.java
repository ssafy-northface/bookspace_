package com.bookspace.domain.comment.service;

import com.bookspace.domain.comment.dao.CommentDao;
import com.bookspace.domain.comment.dto.CommentRequestDto;
import com.bookspace.domain.comment.dto.CommentResponseDto;
import com.bookspace.domain.comment.vo.CommentVo;
import com.bookspace.domain.common.validation.ValidatePostExists;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentDao commentDao;

    // [C]
    @Override
    @ValidatePostExists(postId = "#postId")
    public void createComment(long postId, CommentRequestDto commentDto) {
        CommentVo commentVo = new CommentVo();
        commentVo.setPostId(postId);
        commentVo.setUserId(commentDto.getUserId());
        commentVo.setCommentContent(commentDto.getCommentContent());

        int inserted = commentDao.insertComment(commentVo);

        if (inserted != 1) {
            throw new RuntimeException("Failed to insert comment");
        }
    }

    // [R] - postId
    @Override
    @ValidatePostExists(postId = "#postId")
    public List<CommentResponseDto> getCommentByPostId(long postId) {
        return commentDao.selectCommentsByPostId(postId);
    }

    // [R] - commentId
    @Override
    public CommentResponseDto getCommentByCommentId(long commentId) {

        CommentResponseDto dto = commentDao.selectCommentById(commentId);

        if (dto == null) {
            throw new IllegalArgumentException("Comment not found: " + commentId);
        }

        return dto;
    }

    // [R] - userId
    @Override
    public List<CommentResponseDto> getCommentByUserId(long userId) {

        List<CommentResponseDto> comments = commentDao.selectCommentsByUserId(userId);

        if (comments == null) {
            return List.of();
        }
        return comments;
    }

    // [U] - commentID
    @Override
    public void updateComment(long commentId, CommentRequestDto commentDto) {
        // 1) 해당 comment가 존재하는지 확인
        CommentResponseDto existing = commentDao.selectCommentById(commentId);
        if (existing == null) {
            throw new IllegalArgumentException("Comment not found: " + commentId);
        }

        // 2) 업데이트 값 전달
        CommentVo commentVo = new CommentVo();
        commentVo.setCommentId(commentId);
        commentVo.setCommentContent(commentDto.getCommentContent());

        int updated = commentDao.updateComment(commentVo);

        if (updated != 1) {
            throw new RuntimeException("Failed to update comment: "+commentId);
        }
    }

    // [D] - commentId
    @Override
    public void deleteComment(long commentId) {
        // comment가 존재하는지 확인
        CommentResponseDto existing = commentDao.selectCommentById(commentId);
        if(existing == null){
            throw new IllegalArgumentException("Comment not found: " + commentId);
        }

        // 2) 삭제
        int deleted = commentDao.deleteComment(commentId);
        if (deleted != 1) {
            throw new RuntimeException("Failed to delete comment: "+commentId);
        }
    }




}
