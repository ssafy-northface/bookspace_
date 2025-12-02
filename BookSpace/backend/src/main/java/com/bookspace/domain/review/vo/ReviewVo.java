package com.bookspace.domain.review.vo;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ReviewVo {
    private long reviewId;
    private long bookId;
    private long userId;
    private Double reviewRating;
    private String reviewContent;
    private LocalDateTime reviewDate;
    private LocalDateTime reviewLastModified;
    private LocalDateTime deletedAt;

    private String nickname; // user table과 join해서 가져오는 값
}