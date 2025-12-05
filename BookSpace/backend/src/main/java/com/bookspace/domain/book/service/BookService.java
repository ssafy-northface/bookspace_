package com.bookspace.domain.book.service;

import com.bookspace.domain.book.dto.AladinItemResponseDto;
import com.bookspace.domain.book.dto.BookSearchResponseDto;
import com.bookspace.domain.book.vo.BookVo;

import java.util.List;

public interface BookService {

    // 검색 시 DB 조회 -> 없으면 API 호출
    List<BookSearchResponseDto> searchBooks(String query, String searchType);

    // 유저 액션 (찜, post, review 작성) 시 책이 존재하지 않으면 DB에 책 저장 후 book_id 반환
    Long ensureBookExists(String  isbn);

}
