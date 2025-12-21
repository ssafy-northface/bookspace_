package com.bookspace.domain.review.service;

import com.bookspace.domain.book.service.BookService;
import com.bookspace.domain.review.dto.ReviewRequestDto;
import com.bookspace.domain.review.dao.ReviewDao;
import com.bookspace.domain.review.dto.ReviewResponseDto;
import com.bookspace.domain.review.vo.ReviewVo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewDao reviewDao;
    private final BookService bookService;

    // 1. 리뷰 등록
    @Override
    public void createReview(ReviewRequestDto dto, long loginUserId) {

        String isbn = dto.getIsbn();

        // isbn 없는 경우 예외처리
        if (isbn == null || isbn.isBlank()) {
            throw new RuntimeException("isbn is null or empty");
        }


        // 평점 예외 처리
        // 1. rating 범위 유효성 검사 (0.5~5.0) => 400 BAD_REQUEST
        double rating = dto.getReviewRating();

        if (rating < 0.5 || rating > 5.0) {
            throw new RuntimeException("Rating must be between 0.5 and 5.0.");
        }

        // 2. 0.5 단위만 허용
        if (Math.abs(rating * 2 - Math.round(rating * 2)) > 1e-9) {
            throw new RuntimeException("Rating must be in 0.5 increments.");
        }

        // isbn 기반 DB 책 정보 확인 및 저장
        Long bookId = bookService.ensureBookExists(isbn);

        // 1인 1리뷰 중복 방지
        if (reviewDao.existsByUserIdAndBookId(loginUserId, bookId)) {
            throw new RuntimeException("DUPLICATE_REVIEW");
        }

        // 정상 등록 처리
        ReviewVo vo = new ReviewVo();
        vo.setUserId(loginUserId);
        vo.setBookId(bookId);
        vo.setReviewRating(dto.getReviewRating());
        vo.setReviewContent(dto.getReviewContent());

        int rows = reviewDao.insertReview(vo);
        if( rows != 1 ) {
            throw new RuntimeException("Failed to insert review");
        }
    }


    // 2. 리뷰 수정
    @Override
    public void updateReview(long reviewId, ReviewRequestDto dto, long loginUserId) {

        // 1. 기존 리뷰 조회
        ReviewVo reviewVo = reviewDao.selectReviewById(reviewId);
        if(reviewVo == null) {
            throw new IllegalArgumentException("Review not found with id: " + reviewId);
        }

        // 2. 작성자 검증
        if(reviewVo.getUserId() != loginUserId){
            throw new AccessDeniedException("Access denied 본인의 리뷰만 수정할 수 있습니다.");
        }

        // 3. 평점 유효성 검사 (createReview와 동일 정책)
        double rating = dto.getReviewRating();
        // 1) rating 범위 유효성 검사
        if (rating < 0.5 || rating > 5.0) {
            throw new RuntimeException("Rating must be between 0.5 and 5.0.");
        }

        // 2) 0.5 단위만 허용
        if (Math.abs(rating * 2 - Math.round(rating * 2)) > 1e-9) {
            throw new RuntimeException("Rating must be in 0.5 increments.");
        }

        // 4. 수정 실행
        ReviewVo vo = new ReviewVo();
        vo.setReviewId(reviewId);
        vo.setReviewRating(dto.getReviewRating());
        vo.setReviewContent(dto.getReviewContent());

        int updatedRows = reviewDao.updateReview(vo);
        if (updatedRows == 0) {
            // 존재하지 않는 reviewId일 때
            throw new RuntimeException("Failed to update review with id: " + reviewId);
        }
    }


    // 3. 리뷰 삭제
    @Override
    public void deleteReview(long reviewId, long loginUserId) {

        // 1. 기존 리뷰 조회
        ReviewVo reviewVo = reviewDao.selectReviewById(reviewId);
        if(reviewVo == null) {
            throw new IllegalArgumentException("Review not found with id: " + reviewId);
        }

        // 2. 작성자 검증
        if(reviewVo.getUserId() != loginUserId){
            throw new AccessDeniedException("Access denied 본인의 리뷰만 삭제할 수 있습니다.");
        }


        // 3. 삭제 실행
        int deletedRows = reviewDao.deleteReview(reviewId);
        if (deletedRows == 0) {
            throw new IllegalArgumentException("Failed to delete review with id: " + reviewId);
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
        List<ReviewResponseDto> results = reviewDao.selectReviewsByUserId(userId);
        return results;
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
