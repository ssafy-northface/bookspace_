package com.bookspace.domain.review.dto;

import lombok.Data;

@Data
public class ReviewRequestDto {
    private long userId;
    private String isbn;
    private Double reviewRating;
    private String reviewContent;
    // userId는 보통 세션/토큰에서 가져오므로 여기서 받지 않음!
}
