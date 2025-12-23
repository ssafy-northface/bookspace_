package com.bookspace.domain.aiRecommendation.dto;

import java.util.List;

public class ApiResponseDto {

    // API 연결 테스트용 (디버깅용)
    public String content;

    public boolean is_valid;
    public String user_emotion;
    public String comfort_message;
    public List<AiRecommendationItemDto> books; // ✅ 변경

    public static class AiRecommendationItemDto {
        public String title;
        public String reason;
    }
}
