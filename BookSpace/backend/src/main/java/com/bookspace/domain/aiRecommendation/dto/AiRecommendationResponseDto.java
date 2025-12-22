package com.bookspace.domain.aiRecommendation.dto;

import java.util.List;

public class AiRecommendationResponseDto {

    // API 연결 테스트용
    public String content;

    public boolean is_valid;
    public String user_emotion;
    public String comfort_message;
    public List<BookItem> recommendations;

    // 추후 JSON 파싱용 필드
    public static class BookItem {
        // GPT가 주는 값
        public String isbn13;
        public String theme;
        public String description; // 추천 이유

        // isbn13 기반으로 BookService.getBookDetail로 채울 값 (db 우선 조회 -> 알라딘 API 호출)
        public Long bookId;  // DB에 있으면, 없으면 null
        public String title;
        public String author;
        public String publisher;
        public String cover;
        public Double averageRating; // DB 없으면 0.0 (BookDetailResponseDto 기준)
    }
}
