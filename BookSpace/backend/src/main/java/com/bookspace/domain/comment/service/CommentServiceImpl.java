package com.bookspace.domain.comment.service;

import com.bookspace.domain.comment.dao.CommentDao;
import com.bookspace.domain.comment.dto.CommentRequestDto;
import com.bookspace.domain.comment.dto.CommentResponseDto;
import com.bookspace.domain.comment.vo.CommentVo;
import com.bookspace.domain.common.validation.ValidatePostExists;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentDao commentDao;

    // [C]
    @Override
    @ValidatePostExists(postId = "#postId")
    public void createComment(long postId, CommentRequestDto commentDto, long loginUserId) {
        // 대댓글일 경우 부모 댓글 검증
        if(commentDto.getParentCommentId()!=null){
            CommentResponseDto parent = commentDao.selectCommentById(commentDto.getParentCommentId());
            if(parent == null){
                throw new IllegalArgumentException("Parent comment not found");
            }
            // depth = 1 제한
            if(parent.getParentCommentId()!=null){
                throw new IllegalArgumentException("대댓글에는 답글을 달 수 없습니다.");
            }
            // post 정합성
            if(parent.getPostId()!=postId){
                throw new IllegalArgumentException("잘못된 요청입니다.");
            }
        }

        CommentVo commentVo = new CommentVo();
        commentVo.setPostId(postId);
        commentVo.setUserId(loginUserId);
        commentVo.setParentCommentId(commentDto.getParentCommentId());
        commentVo.setCommentContent(commentDto.getCommentContent());

        int inserted = commentDao.insertComment(commentVo);

        if (inserted != 1) {
            throw new RuntimeException("Failed to insert comment");
        }
    }

    // [R] - postId : 게시글 댓글 + 대댓글 조회
    @Override
    @ValidatePostExists(postId = "#postId")
    public List<CommentResponseDto> getCommentByPostId(long postId) {
        List<CommentResponseDto> flat = commentDao.selectCommentsByPostId(postId);
        Map<Long,CommentResponseDto> map = new HashMap<>();
        List<CommentResponseDto> replies = new ArrayList<>();


        // 부모 댓글 먼저 수집
        for(CommentResponseDto comment : flat){
            if(comment.getParentCommentId()==null){
                map.put(comment.getCommentId(),comment);
            }else{
                replies.add(comment);
            }
        }

        // 대댓글 연결
        for(CommentResponseDto reply : replies){
            CommentResponseDto parent = map.get(reply.getParentCommentId());

            // 부모 없으면 스킵
            if(parent!=null){
                parent.getReplies().add(reply);
            }
        }
        return new ArrayList<>(map.values());
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
    public void updateComment(long commentId, CommentRequestDto commentDto, long loginUserId) {
        // 1) 해당 comment가 존재하는지 확인
        CommentResponseDto existing = commentDao.selectCommentById(commentId);
        if (existing == null) {
            throw new IllegalArgumentException("Comment not found: " + commentId);
        }


        // 2) 권한 체크
        if(existing.getUserId() != loginUserId){
            throw new AccessDeniedException("Access denied 본인의 댓글만 수정할 수 있습니다.");
        }


        // 3) 업데이트 값 전달 - 수정 실행
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
    public void deleteComment(long commentId, long loginUserId) {

        // 1) comment가 존재하는지 확인
        CommentResponseDto existing = commentDao.selectCommentById(commentId);
        if(existing == null){
            throw new IllegalArgumentException("Comment not found: " + commentId);
        }

        // 2) 권한 체크
        if(existing.getUserId() != loginUserId){
            throw new AccessDeniedException("Access Denied  본인의 댓글만 삭제할 수 있습니다.");
        }

        // 3) 삭제
        int deleted = commentDao.deleteComment(commentId);
        if (deleted != 1) {
            throw new RuntimeException("Failed to delete comment: "+commentId);
        }
    }




}
