package com.bookspace.domain.book.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookDetailResponseDto {

    // DB / 알라딘 API 상관없이 도서의 상세정보를 반환할 때 사용
    // 프론트의 BookDetailView에 전달

    // URL 식별자
    private String isbn;

    // DB에 저장된 경우에만 존재 (없으면 null)
    private Long bookId;

    // 책 기본 정보 (DB / 알라딘API)
    private String title;
    private String author;
    private String publisher;
    private String pubDate;
    private String cover;
    private String description;

    // 서비스 메타 정보
    private Double averageRating;  // DB 없으면 0.0
    private Integer reviewCount;   // DB 없으면 0
    private Integer postCount;     // DB 없으면 0
    private Boolean isWished;      // DB에 없거나 비로그인 등은 false
}
