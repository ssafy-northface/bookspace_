package com.bookspace.domain.review.dao;

import com.bookspace.domain.review.dto.ReviewResponseDto;
import org.apache.ibatis.annotations.Mapper;

import com.bookspace.domain.review.vo.ReviewVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReviewDao {

    // 1. 리뷰 등록
    int insertReview(ReviewVo reviewVo);

    // 2. 리뷰 단건 조회
    ReviewVo selectReviewById(@Param("reviewId") Long reviewId);

    // 3. 리뷰 수정
    int updateReview(ReviewVo reviewVo);

    // 4. 리뷰 삭제
    int deleteReview(@Param("reviewId") Long reviewId);

    // 5. 리뷰 조회 (by userId)
    List<ReviewResponseDto> selectReviewsByUserId(@Param("userId") Long userId);

    // 6. 리뷰 조회 (by bookId) (정렬 포함)
    List<ReviewVo> selectReviewsByBookId(
            @Param("bookId") Long bookId,
            @Param("sort") String sort // latest, rating_asc, rating_desc
    );

    // 7. 평균 평점 조회 (by bookId)
    Double selectAverageRating(@Param("bookId") Long bookId);

    // 8. 리뷰 개수 (by bookId)
    Integer countReviews(@Param("bookId") Long bookId);


}
