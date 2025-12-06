package com.bookspace.domain.book.service;

import com.bookspace.domain.book.dto.AladinItemResponseDto;
import com.bookspace.domain.book.dto.BookSearchResponseDto;
import com.bookspace.domain.book.vo.BookVo;

import java.util.List;

public interface BookService {

    // 검색 시 DB 조회 -> 없으면 API 호출
    List<BookSearchResponseDto> searchBooks(String query, String searchType);

    // DB에 없을 경우, 알라딘 api 호출 . DB 저장 -> book_id 반환 (유저 액션: 찜, post, review)
    Long ensureBookExists(String  isbn);

}
