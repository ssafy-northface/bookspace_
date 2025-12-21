package com.bookspace.domain.review.controller;

import java.util.List;

import com.bookspace.global.security.userdetails.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.bookspace.domain.review.dto.ReviewRequestDto;
import com.bookspace.domain.review.dto.ReviewResponseDto;
import com.bookspace.domain.review.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    // 1. 리뷰 등록
    @PostMapping
    public ResponseEntity<String> createReview(
            @RequestBody ReviewRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails user
            ) {
        long loginUserId = user.getUserId();

        reviewService.createReview(dto, loginUserId);
        return ResponseEntity.ok("Review created successfully");
    }

    // 2. 리뷰 수정
    @PutMapping("/{reviewId}")
    public ResponseEntity<String> updateReview(
            @PathVariable long reviewId,
            @RequestBody ReviewRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        long loginUserId = user.getUserId();

        reviewService.updateReview(reviewId, dto, loginUserId);
        return ResponseEntity.ok("Review updated successfully");
    }

    // 3. 리뷰 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(
            @PathVariable long reviewId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        long loginUserId = user.getUserId();

        reviewService.deleteReview(reviewId, loginUserId);
        return ResponseEntity.ok("Review deleted successfully");
    }

    // 4. 리뷰 단건 조회 (by reviewId)
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDto> getReview(@PathVariable long reviewId) {
        return ResponseEntity.ok(reviewService.getReviewById(reviewId));
    }

    // 5. 리뷰 조회 (by user)
    @GetMapping("/me")
    public ResponseEntity<List<ReviewResponseDto>> getMyReviews(@AuthenticationPrincipal CustomUserDetails user) {
        long userId = user.getUserId();
        return ResponseEntity.ok(reviewService.getReviewsByUserId(userId));
    }

    // 6. 리뷰 조회 (by bookId + sort)
    @GetMapping(params = "bookId")
    // /reviews?bookId=10&sort=latest
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByBookId(
            @RequestParam long bookId,
            @RequestParam(defaultValue = "latest") String sort  // latest / rating_asc / rating_desc
    ) {
        return ResponseEntity.ok(reviewService.getReviewsByBookId(bookId, sort));
    }

    // BookService로 넘겨서 BookDetailResponseDTO쪽에서 사용
//    // 7. 특정 책의 평균 평점 조회
//    // /reviews/avg-rating?bookId=10
//    @GetMapping("/avg-rating")
//    public ResponseEntity<Double> getAverageRating(@RequestParam long bookId) {
//        return ResponseEntity.ok(reviewService.getAverageRatingByBookId(bookId));
//    }
//
//    // 8. 특정 책의 리뷰 개수 조회
//    // /reviews/count?bookId=10
//    @GetMapping("/count")
//    public ResponseEntity<Integer> getReviewCount(@RequestParam long bookId) {
//        return ResponseEntity.ok(reviewService.getReviewCountByBookId(bookId));
//    }
}
