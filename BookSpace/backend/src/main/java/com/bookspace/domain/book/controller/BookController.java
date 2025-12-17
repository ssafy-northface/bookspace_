package com.bookspace.domain.book.controller;

import com.bookspace.domain.book.dto.BookDetailResponseDto;
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
//    @GetMapping("/search")
//    public List<BookSearchResponseDto> searchBooks(@RequestParam String query,
//                                                   @RequestParam(name="type",required = false,defaultValue = "title") String searchType){
//        System.out.println("query = " + query + ", type = " + searchType);
//
//        return bookService.searchBooks(query, searchType);
//    }

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

    /**
     * 3. 책 검색 (알라딘 API 기반)
     * - 알라딘 API에서 직접 도서 목록을 조회
     * - DB는 사용하지 않으며, 모든 검색 결과는 알라딘 데이터 기준으로 구성됨
     * - 정렬(sort) 값은 내부적으로 알라딘 API의 정렬 키(PublishTime, SalesPoint 등)로 매핑되어 전달됨
     *
     * @param query 검색어 (필수)
     * @param searchType 검색 카테고리 (책 제목, 저자명, 출판사, ISBN) — 기본값: "title"
     * @param sort 정렬 기준 (latest: 출간일 최신순 / popular: 판매지수순 / accuracy: 정확도순) — 기본값: "latest"
     *
     * @return 알라딘 API에서 조회된 도서 목록
     */
    @GetMapping("/search")
    public List<BookSearchResponseDto> searchBooksFromAladin(
            @RequestParam String query,
            @RequestParam(name = "type", required = false, defaultValue = "title") String searchType,
            @RequestParam(name = "sort", required = false, defaultValue = "latest") String sort
    ) {
        return bookService.searchBooksFromAladin(query, searchType, sort);
    }

    /**
     * 4. 기본 도서 목록 조회 (검색어 없이)
     * - 메인 도서 목록 화면에서 사용하는 기본 리스트
     * - 정렬(sort)을 제공하지 않고, "어떤 목록을 가져올지"만 선택
     *
     * @param type 기본 목록 종류
     *             - new : 신간 전체 목록 (출간일 최신순)
     *             - bestseller : 베스트셀러 목록 (판매지수순)
     *
     * @return 알라딘 ItemList API 기반 기본 도서 목록
     */
    @GetMapping // GET /books
    public List<BookSearchResponseDto> getDefaultBooks(
            @RequestParam(name = "type", required = false, defaultValue = "bestseller") String type
    ) {
        // type: "new" | "bestseller"
        return bookService.getDefaultBooksFromAladin(type);
    }


    /**
     * 5. 도서 상세조회 (ISBN 기반)
     * - DB에 있으면 DB 정보로 반환
     * - DB에 없으면 알라딘 API로 조회하여 반환 (저장 X)
     * - DB에 없는 책은 메타 정보 기본값(0/false) 반환
     *
     * GET /books/detail/{isbn}
     */
    @GetMapping("/detail/{isbn}")
    public BookDetailResponseDto getBookDetail(@PathVariable String isbn) {
        return bookService.getBookDetail(isbn);
    }
}
