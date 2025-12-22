package com.bookspace.domain.aiRecommendation.aiClient;

import com.bookspace.domain.aiRecommendation.dto.AiRecommendationResponseDto;
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

    // ✅ JSON 강제 시스템 프롬프트 (너가 준 프롬프트 + JSON 출력 규칙 추가)
    private static final String SYSTEM_PROMPT = """
# Role
너는 세계 최고의 '심리 기반 도서 큐레이터'이자 '공감적 독서 상담가'야. 
사용자의 짧은 문장에서 숨겨진 감정의 결(뉘앙스)을 읽어내고, 그 마음을 어루만질 수 있는 최적의 책을 추천해줘.

# Task
1. [감정 분석]: 사용자의 입력에서 느껴지는 주요 감정(기쁨, 슬픔, 불안, 분노, 무력감 등)과 그 깊이를 5단계로 분석해.
2. [공감의 말]: 분석된 감정에 대해 진정성 있는 공감의 메시지를 먼저 건네줘.
3. [도서 큐레이션]: 다음 기준에 따라 도서 3권을 추천해.
   - 첫 번째: 현재 감정을 정면으로 마주하고 위로하는 책
   - 두 번째: 감정의 환기를 돕거나 새로운 관점을 제시하는 책
   - 세 번째: 사용자가 지향하면 좋을 긍정적 에너지의 책

# Logic (Chain of Thought)
추천 시 단순히 책 제목만 나열하지 말고, 아래 과정을 거쳐 답변해.
- 이 책의 어떤 구절이나 분위기가 사용자의 현재 감정과 연결되는가?
- 왜 이 타이밍에 이 책을 읽어야 하는가?

# Constraints
- 한국에 정식 출간된 도서 위주로 추천할 것.
- 장르는 소설, 에세이, 인문학, 시집 등 다양하게 고려할 것.
- 답변 톤: 따뜻하고 다정하며 지적인 느낌을 유지할 것.

# Output Format
반드시 아래 JSON 스키마로만 응답해. JSON 이외의 텍스트(설명, 문장, 마크다운, 코드블록)를 절대 출력하지 마.
키 이름은 반드시 아래와 동일하게 사용해.

{
  "is_valid": boolean,
  "user_emotion": string,
  "comfort_message": string,
  "recommendations": [
    { "isbn13": string, "theme": string, "description": string },
    { "isbn13": string, "theme": string, "description": string },
    { "isbn13": string, "theme": string, "description": string }
  ]
}

추가 규칙:
- 사용자 입력이 도서/감정/독서와 무관하면 is_valid=false로 응답하고,
  comfort_message에 "도서 추천 관련 질문만 도와줄 수 있어요"처럼 유도 문구를 넣어.
  이 경우 recommendations는 빈 배열 []로 반환해.
- 추천 도서는 반드시 "isbn13"을 포함해야 하며, 가능한 3권을 채워라.
  ISBN13을 확실히 모르면 그 항목을 추천하지 말고 다른 책으로 대체해.
""";

    /**
     * 사용자 요청을 받아, OpenAI 응답을 "AiRecommendationResponseDto"로 반환한다.
     * - OpenAI 응답은 JSON 문자열이어야 하고, 이 메서드에서 DTO로 파싱한다.
     */
    public AiRecommendationResponseDto requestJson(String userRequest) {
        validateConfig();

        String endpoint = baseUrl + "/api.openai.com/v1/chat/completions";

        // messages 구성
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", SYSTEM_PROMPT));
        messages.add(Map.of("role", "user", "content", userRequest));

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", model);
        body.put("messages", messages);
        body.put("max_tokens", 900);
        body.put("temperature", 0.2); // JSON 안정성 위해 낮게

        // ✅ 가능하면 OpenAI의 json 응답 강제 옵션도 사용 (지원될 때만)
        // 일부 프록시/버전에선 무시될 수 있어도 안전함
        body.put("response_format", Map.of("type", "json_object"));

        String raw = call(endpoint, body);

        // OpenAI 응답에서 assistant content(= JSON 텍스트)만 꺼내기
        String jsonText = extractAssistantContent(raw);

        // 혹시 model이 앞뒤로 텍스트를 섞어 반환했을 때를 대비해 JSON만 잘라내기
        String onlyJson = trimToJsonObject(jsonText);

        // DTO로 파싱
        try {
            AiRecommendationResponseDto dto = om.readValue(onlyJson, AiRecommendationResponseDto.class);

            // 최소 검증: recommendations가 null이면 빈 리스트로
            if (dto.recommendations == null) dto.recommendations = List.of();

            return dto;

        } catch (Exception e) {
            // JSON 파싱 실패 시: 안전한 실패 응답 반환 (서비스가 죽지 않게)
            AiRecommendationResponseDto fallback = new AiRecommendationResponseDto();
            fallback.content = jsonText; // 디버깅용 원문
            fallback.is_valid = false;
            fallback.user_emotion = null;
            fallback.comfort_message = "AI 응답을 처리하는 중 오류가 발생했어요. 잠시 후 다시 시도해주세요.";
            fallback.recommendations = List.of();
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
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(20000);
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
