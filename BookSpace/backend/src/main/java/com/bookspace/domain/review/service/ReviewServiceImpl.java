package com.bookspace.domain.review.service;

import com.bookspace.domain.review.dto.ReviewRequestDto;
import com.bookspace.domain.review.dao.ReviewDao;
import com.bookspace.domain.review.dto.ReviewRequestDto;
import com.bookspace.domain.review.dto.ReviewResponseDto;
import com.bookspace.domain.review.vo.ReviewVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewDao reviewDao;

    // 1. 리뷰 등록
    // 없는 책에 대해 리뷰를 등록하려고 하면 -> 404
    @Override
    public void createReview(ReviewRequestDto dto) {

        // 예외 처리
        // 1. rating 범위 유효성 검사 (0~5) => 400 BAD_REQUEST
        if (dto.getReviewRating() < 0 || dto.getReviewRating() > 5) {
            throw new RuntimeException("Rating must be between 0 and 5.");
        }

        // 2. 소수점 한 자리까지 허용
        if (Math.round(dto.getReviewRating() * 10) != dto.getReviewRating() * 10) {
            throw new RuntimeException("Rating must have at most one decimal place.");
        }

//        // 3. 존재하지 않는 bookId => 404 NOT_FOUND
//        if (bookDao.selectBookById(dto.getBookId()) == null) {
//            throw new IllegalArgumentException("Book not found with id: " + dto.getBookId());
//        }

        // 정상 등록 처리
        ReviewVo vo = new ReviewVo();
        vo.setUserId(dto.getUserId());
        vo.setBookId(dto.getBookId());
        vo.setReviewRating(dto.getReviewRating());
        vo.setReviewContent(dto.getReviewContent());

        reviewDao.insertReview(vo);
    }


    // 2. 리뷰 수정
    @Override
    public void updateReview(long reviewId, ReviewRequestDto dto) {
        ReviewVo vo = new ReviewVo();
        vo.setReviewId(reviewId);
        vo.setReviewRating(dto.getReviewRating());
        vo.setReviewContent(dto.getReviewContent());

        int updatedRows = reviewDao.updateReview(vo);
        if (updatedRows == 0) {
            // 존재하지 않는 reviewId일 때
            throw new IllegalArgumentException("Review not found with id: " + reviewId);
        }
    }

    @Override
    public void deleteReview(long reviewId) {
        int deletedRows = reviewDao.deleteReview(reviewId);
        if (deletedRows == 0) {
            throw new IllegalArgumentException("Review not found with id: " + reviewId);
        }
    }


    // 4. 리뷰 단건 조회 (by reviewId)
    @Override
    public ReviewResponseDto getReviewById(long reviewId) {
        ReviewVo reviewVo = reviewDao.selectReviewById(reviewId);

        if (reviewVo == null) {
            throw new IllegalArgumentException("Review not found with id: " + reviewId);
        }

        return convertToResponseDto(reviewVo);
    }


    // 5. 특정 사용자의 리뷰 목록 조회 (by userId)
    @Override
    public List<ReviewResponseDto> getReviewsByUserId(long userId) {
        List<ReviewVo> reviewVoList = reviewDao.selectReviewsByUserId(userId);
        return reviewVoList.stream()
                .map(this::convertToResponseDto)
                .toList();
    }


    // 6. 특정 책의 리뷰 목록 조회 (by bookId + sort)
    @Override
    public List<ReviewResponseDto> getReviewsByBookId(long bookId, String sort) {

//        // 존재하지 않는 책이면 404
//        if (bookDao.selectBookById(bookId) == null) {
//            throw new IllegalArgumentException("Book not found with id: " + bookId);
//        }

        List<ReviewVo> reviewVoList = reviewDao.selectReviewsByBookId(bookId, sort);
        return reviewVoList.stream()
                .map(this::convertToResponseDto)
                .toList();
    }


    // 7. 특정 책의 평균 평점 조회
    @Override
    public Double getAverageRatingByBookId(long bookId) {
        return reviewDao.selectAverageRating(bookId);
    }


    // 8. 특정 책의 리뷰 개수 조회
    @Override
    public Integer getReviewCountByBookId(long bookId) {
        return reviewDao.countReviews(bookId);
    }



    private ReviewResponseDto convertToResponseDto(ReviewVo review) {
        if (review == null) return null;

        ReviewResponseDto dto = new ReviewResponseDto();
        dto.setReviewId(review.getReviewId());
        dto.setBookId(review.getBookId());
        dto.setUserId(review.getUserId());
        dto.setNickname(review.getNickname());           // bookId 조회 시 JOIN으로 채워짐
        dto.setReviewRating(review.getReviewRating());
        dto.setReviewContent(review.getReviewContent());
        dto.setReviewDate(review.getReviewDate());
        dto.setReviewLastModified(review.getReviewLastModified());

        return dto;
    }

}
