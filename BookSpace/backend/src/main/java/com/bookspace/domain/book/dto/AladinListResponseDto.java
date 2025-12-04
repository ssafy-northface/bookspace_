package com.bookspace.domain.book.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 알라딘 ItemSearch - ItemList 응답 전체를 받는 DTO
 */

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AladinListResponseDto {
    @JsonProperty("totalResults")
    private Integer totalResults;

    @JsonProperty("startIndex")
    private Integer startIndex;

    @JsonProperty("itemsPerPage")
    private Integer itemsPerPage;

    @JsonProperty("query")
    private String query;

    @JsonProperty("searchCategoryId")
    private Integer searchCategoryId;

    @JsonProperty("searchCategoryName")
    private String searchCategoryName;

    @JsonProperty("item")
    private List<AladinItemResponseDto> items;

}
