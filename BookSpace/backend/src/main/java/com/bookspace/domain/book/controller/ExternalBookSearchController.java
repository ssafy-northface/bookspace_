package com.bookspace.domain.book.controller;

import com.bookspace.domain.book.dto.BookSearchResponseDto;
import com.bookspace.domain.book.service.BookSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 알라딘 api 도서 검색용 (프론트에서 직접 호출 x)
 */

@RestController
@RequiredArgsConstructor
@RequestMapping ("/external/books")
public class ExternalBookSearchController {

    private final BookSearchService bookSearchService;

    // 도서 검색 (쿼리, 사이즈 -> 알라딘 api)
    @GetMapping("/search") // 프론트: GET /external/books/search?query=해리포터&size=20
    public ResponseEntity<List<BookSearchResponseDto>> searchBooks(@RequestParam String query, @RequestParam(defaultValue = "10") int size) {
        List<BookSearchResponseDto> result = bookSearchService.searchBooks(query, size);
        return ResponseEntity.ok(result);
    }

}
