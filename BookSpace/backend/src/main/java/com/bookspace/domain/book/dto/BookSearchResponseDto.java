package com.bookspace.domain.book.dto;

import lombok.Data;

/**
 * 프론트 검색창 - 검색 결과 리스트에 대한 응답 DTO
 */
@Data
public class BookSearchResponseDto {

    private String title;
    private String author;
    private String publisher;
    private String pubDate;
    private String isbn13;
    private String cover; // 책 표지 url
    private String categoryName;
}
