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
    - 입력값이 "안녕", "배고파" 등 도서 추천과 무관한 경우 is_valid를 false로 반환.
    
    Example 1 (Invalid Input):
    Input: "지금 점심 메뉴 추천해줘."
    Output: {
      "is_valid": false,
      "user_emotion": "도서 추천과 무관한 일상적인 질문",
      "comfort_message": "저는 당신의 마음을 읽는 독서 메이트예요! 지금 느끼는 감정을 알려주시면 그에 딱 맞는 책을 추천해 드릴게요.",
      "book_titles": []
    }

    # Final Instruction
    반드시 위의 JSON 형식만 출력하며, 마크다운 코드 블록(```json)을 포함하지 않은 순수 JSON 텍스트만 반환하라.
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
            if (dto.book_titles == null) dto.book_titles = List.of();

            return dto;

        } catch (Exception e) {
            // JSON 파싱 실패 시: 안전한 실패 응답 반환 (서비스가 죽지 않게)
            ApiResponseDto fallback = new ApiResponseDto();
            fallback.content = jsonText; // 디버깅용 원문
            fallback.is_valid = false;
            fallback.user_emotion = null;
            fallback.comfort_message = "AI 응답을 처리하는 중 오류가 발생했어요. 잠시 후 다시 시도해주세요.";
            fallback.book_titles = List.of();
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
