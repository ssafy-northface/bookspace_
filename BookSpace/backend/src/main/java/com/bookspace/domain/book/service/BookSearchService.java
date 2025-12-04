package com.bookspace.domain.book.service;

import com.bookspace.domain.book.dto.BookSearchResponseDto;

import java.util.List;

public interface BookSearchService {
    // 검색어 -> 알라딘 api 호출
    List<BookSearchResponseDto> searchBooks(String query, int size);
;}
