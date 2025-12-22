package com.bookspace.domain.aiRecommendation.dto;

import java.util.List;

public class AiRecommendationResponseDto {

    public boolean is_valid;
    public String user_emotion;
    public String comfort_message;
    public List<BookItem> recommendations;

    public static class BookItem {
        public String isbn13;
        public String theme;
        public String description;
    }
}
