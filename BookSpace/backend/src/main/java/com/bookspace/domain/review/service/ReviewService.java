package com.bookspace.domain.review.service;

import com.bookspace.domain.review.dto.ReviewRequestDto;
import com.bookspace.domain.review.dto.ReviewResponseDto;

import java.util.List;

public interface ReviewService {

    // 1. 리뷰 등록
    void createReview(ReviewRequestDto requestDto, long loginUserId);

    // 2. 리뷰 수정
    void updateReview(long reviewId, ReviewRequestDto requestDto, long loginUserId);

    // 3. 리뷰 삭제
    void deleteReview(long reviewId, long loginUserId);

    // 4. 리뷰 단건 조회 (by reviewId)
    ReviewResponseDto getReviewById(long reviewId);

    // 5. 리뷰 조회 (by userId)
    List<ReviewResponseDto> getReviewsByUserId(long userId);

    // 6. 리뷰 조회 (by bookId)
    List<ReviewResponseDto> getReviewsByBookId(long bookId, String sort);

    // 7. 책의 평균 평점
    Double getAverageRatingByBookId(long bookId);

    // 8. 책의 리뷰 개수
    Integer getReviewCountByBookId(long bookId);


}
