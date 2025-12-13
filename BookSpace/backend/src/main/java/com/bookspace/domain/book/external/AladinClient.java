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
    private static final String ITEM_LIST_URL   = "https://www.aladin.co.kr/ttb/api/ItemList.aspx"; // 기본 목록 조회용

    private final RestTemplate restTemplate = new RestTemplate(); // http 요청
    private final ObjectMapper objectMapper = new ObjectMapper(); // json 파싱

    // 기존에 파라미터 query와 searchType 2개만 받는 메서드 유지 (오버로드 메서드)
    // 다른 파트에서 파라미터 Sort가 포함되지 않은 searchBooks()를 호출하고 있을수도 있기 때문에
    // Sort의 default값을 설정하여 에러발생 방지
    public AladinListResponseDto searchBooks(String query, String searchType, int maxResults) {
        return searchBooks(query, searchType, "latest", maxResults);
    }

    // query, searchType, sort => 3개의 파라미터
    public AladinListResponseDto searchBooks(String query, String searchType, String sort, int maxResults){
        try{
            // 검색 타입 -> 알라딘 api 쿼리 타입에 맞게 설정
            String queryType = switch (searchType) {
                case "author" -> "Author";
                case "publisher" -> "Publisher";
                case "isbn" -> "ISBN";
                default -> "Title";
            };

            // 정렬 기준 -> 알라딘 Sort 파라미터로 매핑
            String sortKey = (sort == null || sort.isBlank()) ? "latest" : sort.toLowerCase();
            String sortType = switch (sortKey) {
                case "latest" -> "PublishTime";   // 출간일 최신순
                case "popular" -> "SalesPoint";   // 판매지수(인기순)
                case "accuracy" -> "Accuracy";   // 정확도순
                default -> "PublishTime";         // 기본: 출간일순
            };


            // 요청 url 구성
            // sort 추가
            String url = UriComponentsBuilder.fromHttpUrl(ITEM_SEARCH_URL)
                    .queryParam("ttbkey", ttbKey)
                    .queryParam("Query", query)
                    .queryParam("QueryType", queryType)
                    .queryParam("Sort", sortType)
                    .queryParam("MaxResults", maxResults)
                    .queryParam("start", 1)
                    .queryParam("SearchTarget", "Book")
                    .queryParam("Cover", "Big")
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

    // 검색어 없이 기본 도서 목록 조회 (/books 메인화면에 보여질 기본 도서 목록)
    public AladinListResponseDto fetchDefaultBooks(String type, int maxResults) {
        try {
            String key = (type == null || type.isBlank()) ? "bestseller" : type.toLowerCase();

            String queryType;  // ItemList API의 QueryType
            String sortType;   // 알라딘 Sort

            switch (key) {
                case "bestseller" -> {
                    // 베스트셀러 목록 (인기순)
                    queryType = "BestSeller";
                    sortType = "SalesPoint";
                }
                case "new" -> {
                    // 신간 전체 (출간일 최신순)
                    queryType = "ItemNewAll";
                    sortType = "PublishTime";
                }
                default -> {
                    // 이상한 값 오면 기본은 신간 최신순
                    queryType = "ItemNewAll";
                    sortType = "PublishTime";
                }
            }

            String url = UriComponentsBuilder.fromHttpUrl(ITEM_LIST_URL)
                    .queryParam("ttbkey", ttbKey)
                    .queryParam("QueryType", queryType)
                    .queryParam("Sort", sortType)
                    .queryParam("MaxResults", maxResults)
                    .queryParam("start", 1)
                    .queryParam("SearchTarget", "Book")
                    .queryParam("Cover", "Big")
                    .queryParam("output", "js")
                    .queryParam("Version", "20131101")
                    .build()
                    .toUriString();

            log.info("[Aladin][Default] request URL = {}", url);

            String responseBody = restTemplate.getForObject(url, String.class);

            log.info("[Aladin][Default] raw response = {}", responseBody);
            if (responseBody != null && responseBody.contains("\"errorCode\"")) {
                log.error("[Aladin][Default] ERROR RESPONSE: {}", responseBody);
                return null;
            }

            return objectMapper.readValue(responseBody, AladinListResponseDto.class);
        } catch (Exception e) {
            log.error("알라딘 API 기본 도서 목록 조회 실패", e);
            return null;
        }
    }
}
