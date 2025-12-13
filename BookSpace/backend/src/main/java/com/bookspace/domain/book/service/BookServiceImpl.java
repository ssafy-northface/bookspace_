package com.bookspace.domain.book.service;

import com.bookspace.domain.book.dao.BookDao;
import com.bookspace.domain.book.dto.AladinItemResponseDto;
import com.bookspace.domain.book.dto.AladinListResponseDto;
import com.bookspace.domain.book.dto.BookSearchResponseDto;
import com.bookspace.domain.book.external.AladinClient;
import com.bookspace.domain.book.vo.BookVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bookspace.domain.book.converter.BookConverter;


import java.awt.print.Book;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookDao bookDao;
    private final AladinClient aladinClient;


    // [검색] DB 검색 시 없으면 알라딘 API 호출
    @Override
    public List<BookSearchResponseDto> searchBooks(String query, String searchType) {

        // 1) DB 우선 검색
        List<BookSearchResponseDto> dbResult = searchBooksFromDb(query, searchType);
        if(!dbResult.isEmpty()){
            return dbResult;
        }
        // 없으면 API 호출
        return searchBooksFromAladin(query, searchType);
    }

    // [검색] DB
    private List<BookSearchResponseDto> searchBooksFromDb(String query, String searchType) {
        List<BookVo> dbResponse = bookDao.searchBooks(query, searchType);

        if(dbResponse==null||dbResponse.isEmpty()){
            return List.of();
        }

        return dbResponse.stream().map(BookConverter::toSearchResponseFromBookVo).toList(); // BookVo -> BookSearchResponseDto
    }

    // [검색] 알라딘 api
    private List<BookSearchResponseDto> searchBooksFromAladin(String query, String searchType) {
        AladinListResponseDto apiResponse = aladinClient.searchBooks(query, searchType, 10);

        if(apiResponse==null||apiResponse.getItems()==null){
            return List.of();
        }

        return apiResponse.getItems().stream().map(BookConverter::toSearchResponseFromAladinItem).toList();
    }


    /**
     * [조회] DB에서 isbn으로 bookId 조회 => return bookId
     */
    @Transactional
    public Long findBookIdByIsbnFromDb(String isbn){
        if(isbn == null||isbn.isBlank()){
            throw new IllegalArgumentException("isbn is null or empty.");
        }

        BookVo existing = bookDao.findBookByIsbn(isbn);
        return (existing == null) ? null : existing.getBookId();
    }


    /**
     * [조회 & 저장] isbn 값으로 알라딘 api 호출 후, 해당 isbn 값을 가진 도서를 DB에 저장 후 bookId 반환 (저장용)
     * !! 사용하기 전 DB에 책이 없는지 확인해야 함!! (protected)
     */
    @Transactional
    protected Long fetchAndSaveBookByIsbnFromAladin(String isbn){
        AladinListResponseDto apiResponse = aladinClient.searchBooks(isbn, "isbn",1);

        if(apiResponse==null || apiResponse.getItems()==null){
            throw new IllegalArgumentException("Book Not Found for ISBN:"+isbn);
        }

        AladinItemResponseDto item = apiResponse.getItems().get(0);

        BookVo newBookVo = BookConverter.toBookVoFromAladinItem(item); // 알라딘 응답 -> BookVo
        bookDao.insertBook(newBookVo);

        return newBookVo.getBookId();
    }

    /**
     * [유저 액션용]: 책 찾기 + (없으면 저장) => return bookId
     * DB에서 isbn에 해당하는 책 조회 후  (findBookByIsbn)
     * 없으면 알라딘 api 호출후 DB에 해당 책 저장 (fetchAndSaveBookByIsbnFromAladin)
      */
    @Override
    @Transactional
    public Long ensureBookExists(String isbn) {
        if(isbn == null || isbn.isBlank()){
            throw new IllegalArgumentException("isbn is null or empty");
        }
        // DB에서 isbn 조회
        Long existingBookId = findBookIdByIsbnFromDb(isbn);
        if(existingBookId!=null){
            return existingBookId;
        }

        // DB에 없는 경우 알라딘 api 호출 후 DB에 책 저장
        return fetchAndSaveBookByIsbnFromAladin(isbn);
    }

    // 알라딘 API로만 책 조회 (3개의 파라미터)
    @Override
    public List<BookSearchResponseDto> searchBooksFromAladin(String query, String searchType, String sort) {
        AladinListResponseDto apiResponse =
                aladinClient.searchBooks(query, searchType, sort, 20);

        if (apiResponse == null || apiResponse.getItems() == null) {
            return List.of();
        }

        return apiResponse.getItems().stream()
                .map(BookConverter::toSearchResponseFromAladinItem)
                .toList();
    }

    /**
     * [기본 목록] 검색어 없이 알라딘에서 기본 도서 리스트 조회
     * - 메인 도서 목록 화면에서 사용
     * - type: new | bestseller
     */
    @Override
    public List<BookSearchResponseDto> getDefaultBooksFromAladin(String type) {
        // type: "new" | "bestseller"
        AladinListResponseDto apiResponse =
                aladinClient.fetchDefaultBooks(type, 100);

        if (apiResponse == null || apiResponse.getItems() == null) {
            return List.of();
        }

        return apiResponse.getItems().stream()
                .map(BookConverter::toSearchResponseFromAladinItem)
                .toList();
    }
}
