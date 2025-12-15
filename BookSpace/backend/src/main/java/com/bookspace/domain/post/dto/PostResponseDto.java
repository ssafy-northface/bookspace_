package com.bookspace.domain.post.dto;

import lombok.Data;
import java.time.LocalDateTime;

// 단건 조회에서 내려줄 Dto
@Data
public class PostResponseDto {
    private long postId;
    private long userId;
    private long bookId;

    private String bookTitle;
    private String bookAuthor;
    private String bookImageUrl;

    private String postTitle;
    private String postContent;
    private LocalDateTime postDate;
    private int postViewCnt;
    private LocalDateTime postLastModified;

    private int likeCount; // 해당 post의 좋아요 총 개수
    private boolean liked; // 로그인한 유저라면 유저가 좋아요를 눌렀는지
    private int commentCount;

    private String userNickName;
}