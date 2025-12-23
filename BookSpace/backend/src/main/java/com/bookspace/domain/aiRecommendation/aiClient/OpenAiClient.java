package com.bookspace.domain.aiRecommendation.aiClient;

import com.bookspace.domain.aiRecommendation.dto.ApiResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class OpenAiClient {

    @Value("${gms.api.provider-key}")
    private String apiKey;

    @Value("${gms.api.base-url}")
    private String baseUrl;

    @Value("${gms.api.openai.model}")
    private String model;

    private static final ObjectMapper om = new ObjectMapper();

    // JSON 강제 시스템 프롬프트 (너가 준 프롬프트 + JSON 출력 규칙 추가)
    private static final String SYSTEM_PROMPT = """
             #Role
             너는 10000권의 책을 알고 있는 도서관 사서야.\s
             사용자의 문장에서 사용자의 상황과 심리를 파악하여 책을 추천해주는 전문가야.
            
             # Task
             1. [감정 분석]: 사용자의 입력에서 느껴지는 주요 감정(기쁨, 슬픔, 불안, 분노, 무력감 등)을 파악해
             2. [공감의 말]: 분석된 감정에 대해 진정성 있는 공감의 메시지를 먼저 건네줘.
       
            
             # Logic (Chain of Thought)
             - 알라딘, 교보문고, 예스24 등 한국의 도서 판매 플랫폼의 도서 책 소개 혹은 줄거리 등을 참고해.
             - 베스트셀러로 추천해.
             - 사용자의 상황에 맞는 도서를 추천해.
            
             # Constraints
             - 한국에 정식 출간된 도서 위주로 추천할 것.
             - 장르는 소설, 에세이, 인문학, 시집 등 다양하게 고려할 것.
             - 도서의 정확한 제목만 쓰고, 부제 혹은 시리즈 정보는 제공하지 말 것.
             - 답변 톤: 따뜻하고 다정하며 지적인 느낌을 유지할 것.
            
             # Output Format
             반드시 아래 JSON 스키마로만 응답해. JSON 이외의 텍스트(설명, 문장, 마크다운, 코드블록)를 절대 출력하지 마.
             키 이름은 반드시 아래와 동일하게 사용해.
              
              Output: {
              "is_valid": true,
              "user_emotion": "도서 추천과 무관한 일상적인 질문",
              "comfort_message": "저는 당신의 마음을 읽는 독서 메이트예요! 지금 느끼는 감정을 알려주시면 그에 딱 맞는 책을 추천해 드릴게요.",
              "books": [
                 {
                    "title": "추천하는 도서 제목",
                    "reason": "추천이유 1문장"
                 }
               ]
             }
            
              Output: {
              "is_valid": false,
              "user_emotion": "도서 추천과 무관한 일상적인 질문",
              "comfort_message": "저는 당신의 마음을 읽는 독서 메이트예요! 지금 느끼는 감정을 알려주시면 그에 딱 맞는 책을 추천해 드릴게요.",
              "books": []
              }
            
             추가 규칙:
             - 사용자 입력이 도서/감정/독서와 무관하면 is_valid=false로 응답하고,
               comfort_message에 "도서 추천 관련 질문만 도와줄 수 있어요"처럼 유도 문구를 넣어.
               이 경우 recommendations는 빈 배열 []로 반환해.
             - 추천 도서의 제목은 정확해야 해.
             ""\";
                                                                                                    
""";


    /**
     * 사용자 요청을 받아, OpenAI 응답을 "AiRecommendationResponseDto"로 반환한다.
     * - OpenAI 응답은 JSON 문자열이어야 하고, 이 메서드에서 DTO로 파싱한다.
     */
    public ApiResponseDto requestJson(String userRequest) {
        validateConfig();

        String endpoint = baseUrl + "/api.openai.com/v1/chat/completions";

        // messages 구성
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", SYSTEM_PROMPT));
        messages.add(Map.of("role", "user", "content", userRequest));

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", model);
        body.put("messages", messages);
//        body.put("max_tokens", 900);
//        body.put("temperature", 0.2); // JSON 안정성 위해 낮게

        // 가능하면 OpenAI의 json 응답 강제 옵션도 사용 (지원될 때만)
        // 일부 프록시/버전에선 무시될 수 있어도 안전함
        body.put("response_format", Map.of("type", "json_object"));

        String raw = call(endpoint, body);

        // OpenAI 응답에서 assistant content(= JSON 텍스트)만 꺼내기
        String jsonText = extractAssistantContent(raw);

        // 혹시 model이 앞뒤로 텍스트를 섞어 반환했을 때를 대비해 JSON만 잘라내기
        String onlyJson = trimToJsonObject(jsonText);

        // DTO로 파싱
        try {
            ApiResponseDto dto = om.readValue(onlyJson, ApiResponseDto.class);

            // 최소 검증: book_titles가 null이면 빈 리스트로
            if (dto.books == null) dto.books = List.of();

            return dto;

        } catch (Exception e) {
            // JSON 파싱 실패 시: 안전한 실패 응답 반환 (서비스가 죽지 않게)
            ApiResponseDto fallback = new ApiResponseDto();
            fallback.content = jsonText; // 디버깅용 원문
            fallback.is_valid = false;
            fallback.user_emotion = null;
            fallback.comfort_message = "AI 응답을 처리하는 중 오류가 발생했어요. 잠시 후 다시 시도해주세요.";
            fallback.books = List.of();
            return fallback;
        }
    }

    // -----------------------------
    // HTTP Call
    // -----------------------------

    private String call(String endpoint, Map<String, Object> body) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(endpoint).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
//            conn.setConnectTimeout(8000);
//            conn.setReadTimeout(20000);
            conn.setDoOutput(true);

            byte[] payload = om.writeValueAsBytes(body);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload);
            }

            int code = conn.getResponseCode();
            InputStream is = (code >= 200 && code < 300) ? conn.getInputStream() : conn.getErrorStream();

            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
            }

            if (code < 200 || code >= 300) {
                throw new IllegalStateException("OpenAI 호출 실패 status=" + code + ", body=" + sb);
            }

            return sb.toString();

        } catch (IOException e) {
            throw new IllegalStateException("OpenAI 호출 중 네트워크 오류", e);
        }
    }

    // -----------------------------
    // Response Parsing helpers
    // -----------------------------

    /**
     * OpenAI /chat/completions 응답에서 choices[0].message.content 텍스트를 추출한다.
     */
    private String extractAssistantContent(String raw) {
        try {
            JsonNode root = om.readTree(raw);
            JsonNode contentNode = root.path("choices").get(0).path("message").path("content");
            if (contentNode.isMissingNode() || contentNode.isNull()) return "";
            return contentNode.asText();
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    /**
     * 혹시 content 앞뒤로 잡텍스트가 붙으면, 첫 '{'부터 마지막 '}'까지 잘라 JSON만 남긴다.
     */
    private String trimToJsonObject(String text) {
        if (text == null) return "";
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start < 0 || end < 0 || end <= start) return text.trim();
        return text.substring(start, end + 1).trim();
    }

    private void validateConfig() {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("GMS_PROVIDER_KEY가 설정되지 않았습니다.");
        }
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException("gms.api.base-url이 설정되지 않았습니다.");
        }
        if (model == null || model.isBlank()) {
            throw new IllegalStateException("gms.api.openai.model이 설정되지 않았습니다.");
        }
    }
}
