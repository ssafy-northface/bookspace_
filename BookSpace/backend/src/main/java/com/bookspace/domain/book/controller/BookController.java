package com.bookspace.domain.book.controller;

import com.bookspace.domain.book.dto.BookSearchResponseDto;
import com.bookspace.domain.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    /**
     * 1. 책 검색 (카테고리별 검색)
     * DB 먼저 조회 -> 없으면 알라딘 api 호출 (여기까지 조회용 -> DB 저장 X)
     * @param query 검색어
     * @param searchType 검색 카테고리 (책 이름, 저자명, 출판사)
     */
    @GetMapping("/search")
    public List<BookSearchResponseDto> searchBooks(@RequestParam String query,
                                                   @RequestParam(name="type",required = false,defaultValue = "title") String searchType){
        System.out.println("query = " + query + ", type = " + searchType);

        return bookService.searchBooks(query, searchType);
    }

    /**
     * 2. ISBN 기반 단건 조회: 유저 액션 (찜, 포스트, 리뷰 작성) 처리할 때 DB에서 bookId 생성용
     * DB에 있으면 bookId return
     * 없으면 알라딘 api 호출 -> DB 저장 -> bookId return
     */
    @GetMapping("/{isbn}")
    public BookSearchResponseDto getBook(@PathVariable String isbn){
        Long bookId = bookService.ensureBookExists(isbn); // isbn에 해당하는 책 추가
        //return bookService.searchBooks(isbn).get(0);
        return null;
    }
}
