package com.bookspace.domain.book.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 알라딘 API 응답 배열의 개별 도서 정보
 */

@Data
//외부 API 응답(JSON)이 자바 DTO와 1:1로 정확히 일치하지 않을 때
// JSON -> 객체 변환 과정에서 존재하지 않는 필드가 들어오더라도 무시하고 매핑 허용 옵션
@JsonIgnoreProperties(ignoreUnknown = true)
public class AladinItemResponseDto {

    @JsonProperty("title")
    private String title;

    @JsonProperty("author")
    private String author;

    @JsonProperty("publisher")
    private String publisher;

    @JsonProperty("pubDate")
    private String pubDate;

    @JsonProperty("isbn13")
    private String isbn13;

    @JsonProperty("cover")
    private String cover;

    @JsonProperty("categoryName")
    private String categoryName;

    @JsonProperty("description")
    private String description;

    @JsonProperty("salesPoint")
    private Integer salesPoint;


    @JsonProperty("priceSales")
    private Integer priceSales;

    @JsonProperty("priceStandard")
    private Integer priceStandard;
}
