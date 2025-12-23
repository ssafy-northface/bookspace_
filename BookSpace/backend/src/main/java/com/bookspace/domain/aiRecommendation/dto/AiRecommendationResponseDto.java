package com.bookspace.domain.aiRecommendation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AiRecommendationResponseDto {

    // 추천 성공 여부
    private boolean isValid;

    // 감정 분석/공감 문구 (AI 결과를 그대로 내려주면 UX 좋아짐)
    private String userEmotion;
    private String comfortMessage;

    // 최종 확정된 도서 카드 (최대 3권)
    private List<BookItem> recommendations;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class BookItem {

        private Long bookId;

        private String isbn13;
        private String title;
        private String author;
        private String publisher;
        private String cover;

        private Double averageRating; // DB에 있는 책이면 평점, 없으면 0.0
    }
}
