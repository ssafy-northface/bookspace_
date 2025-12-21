package com.bookspace.domain.review.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ReviewResponseDto {

    private long reviewId;
    private long bookId;

    private String bookIsbn;
    private String bookTitle;
    private String bookImageUrl;
    private String bookAuthor;

    private long userId;
    private String nickname; // 사용자 닉네임 (JOIN 해서 가져옴)
    private Double reviewRating;
    private String reviewContent;
    private LocalDateTime reviewDate;
    private LocalDateTime reviewLastModified;
}