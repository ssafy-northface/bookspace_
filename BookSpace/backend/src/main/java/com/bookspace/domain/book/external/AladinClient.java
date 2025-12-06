package com.bookspace.domain.book.external;

import com.bookspace.domain.book.dto.AladinListResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;

/**
 * 알라딘 API 호출 담당 클라이언트
 * - ttbKey는 env, application.yaml에서 주입
 */
@Slf4j
@Component
public class AladinClient {

    @Value("${aladin.ttbkey}")
    private String ttbKey;

    private static final String ITEM_SEARCH_URL = "https://www.aladin.co.kr/ttb/api/ItemSearch.aspx";

    private final RestTemplate restTemplate = new RestTemplate(); // http 요청
    private final ObjectMapper objectMapper = new ObjectMapper(); // json 파싱

    public AladinListResponseDto searchBooks(String query, String searchType, int maxResults){
        try{
            // 검색 타입 -> 알라딘 api 쿼리 타입에 맞게 설정
            String queryType = switch (searchType) {
                case "author" -> "Author";
                case "publisher" -> "Publisher";
                default -> "Title";
            };

            // 요청 url 구성
            String url = UriComponentsBuilder.fromHttpUrl(ITEM_SEARCH_URL)
                    .queryParam("ttbkey", ttbKey)
                    .queryParam("Query", query)
                    .queryParam("QueryType", queryType)
                    .queryParam("MaxResults", maxResults)
                    .queryParam("start", 1)
                    .queryParam("SearchTarget", "Book")
                    .queryParam("output", "js")
                    .queryParam("Version", "20131101")
                    .build()
                    //.encode(StandardCharsets.UTF_8) // 한글 인코딩
                    .toUriString();

            log.info("[Aladin] request URL = {}", url);

            String responseBody = restTemplate.getForObject(url, String.class);

            // 에러 확인용
            log.info("[Aladin] raw response = {}", responseBody);
            if (responseBody != null && responseBody.contains("\"errorCode\"")) {
                log.error("[Aladin] ERROR RESPONSE: {}", responseBody);
                return null;
            }

            return objectMapper.readValue(responseBody, AladinListResponseDto.class);
        }
        catch(Exception e){
            log.error("알라딘 API 도서 검색 실패",query, e);
            return null;
        }
    }
}
