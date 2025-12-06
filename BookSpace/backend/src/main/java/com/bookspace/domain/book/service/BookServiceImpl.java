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

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookDao bookDao;
    private final AladinClient aladinClient;


    // DB 검색 시 없으면 알라딘 API 호출
    @Override
    public List<BookSearchResponseDto> searchBooks(String query, String searchType) {
        // DB 검색
        List<BookVo> dbResponse = bookDao.searchBooks(query, searchType);

        if(!dbResponse.isEmpty()){
            return dbResponse.stream().map(this::toSearchDto).toList(); // BookVo -> BookSearchResponseDto
        }
        // 없으면 API 호출
        AladinListResponseDto apiResponse = aladinClient.searchBooks(query,searchType, 10);

        if(apiResponse== null || apiResponse.getItems()==null){
            return List.of();
        }

        // 3) api 결과를 DTO로 변환한 후 반환
        return apiResponse.getItems().stream().map(this::toSearchDto).toList();
    }

    /**
     * isbn 값으로 알라딘 api 호출 후, 해당 isbn 값을 가진 도서를 DB에 저장 후 bookId 반환 (저장용)
     * !! 사용하기 전 DB에 책이 없는지 확인해야 함!!
     */
    @Transactional
    protected Long fetachAndSaveBookByIsbnFromAladin(String isbn){
        AladinListResponseDto apiResponse = aladinClient.searchBooks(isbn, "isbn",1);

        if(apiResponse==null || apiResponse.getItems()==null){
            throw new IllegalArgumentException("Book Not Found for ISBN:"+isbn);
        }

        AladinItemResponseDto item = apiResponse.getItems().get(0);

        BookVo newBookVo = toBookVo(item); // 알라딘 응답 -> BookVo
        bookDao.insertBook(newBookVo);

        return newBookVo.getBookId();
    }

    /**
     * DB에서 isbn에 해당하는 책 조회 후  (findBookByIsbn)
     * 없으면 알라딘 api 호출후 DB에 해당 책 저장 (fetachAndSaveBookByIsbnFromAladin)
      */
    @Override
    @Transactional
    public Long ensureBookExists(String isbn) {
        if(isbn == null || isbn.isBlank()){
            throw new IllegalArgumentException("isbn is null or empty");
        }
        // DB에서 isbn 조회
        BookVo existing = bookDao.findBookByIsbn(isbn);
        if(existing!=null){
            return existing.getBookId();
        }

        // DB에 없는 경우 알라딘 api 호출 후 DB에 책 저장
        return fetachAndSaveBookByIsbnFromAladin(isbn);
    }

    // --- converter methods  ---

    // 알라딘 api 호출 후 응답을 BookVo 타입으로 바꾸기
    private BookVo toBookVo(AladinItemResponseDto item) {
        BookVo vo = new BookVo();
        vo.setBookTitle(item.getTitle());
        vo.setBookAuthor(item.getAuthor());
        vo.setBookPublisher(item.getPublisher());
        vo.setBookPublicationDate(item.getPubDate());
        vo.setBookIsbn(item.getIsbn13());
        vo.setBookDescription(item.getDescription());
        vo.setBookPrice(item.getPriceStandard());
        vo.setBookImageUrl(item.getCover());
        vo.setBookSalesPoint(item.getSalesPoint());
        vo.setBookCategory(item.getCategoryName());
        return vo;
    }

    // DB 조회 결과 VO -> 검색 응답 DTO 타입으로 바꾸기
    private BookSearchResponseDto toSearchDto(BookVo vo) {
        BookSearchResponseDto dto = new BookSearchResponseDto();
        dto.setTitle(vo.getBookTitle());
        dto.setAuthor(vo.getBookAuthor());
        dto.setPublisher(vo.getBookPublisher());
        dto.setPubDate(vo.getBookPublicationDate());
        dto.setIsbn13(vo.getBookIsbn());
        dto.setCover(vo.getBookImageUrl());
        dto.setCategoryName(vo.getBookCategory());
        return dto;
    }

    // 알라딘 조회 결과 DTO를 책 검색 결과 응답 DTO 타입으로 바꾸기
    private BookSearchResponseDto toSearchDto(AladinItemResponseDto item) {
        BookSearchResponseDto dto = new BookSearchResponseDto();
        dto.setTitle(item.getTitle());
        dto.setAuthor(item.getAuthor());
        dto.setPublisher(item.getPublisher());
        dto.setPubDate(item.getPubDate());
        dto.setIsbn13(item.getIsbn13());
        dto.setCover(item.getCover());
        dto.setCategoryName(item.getCategoryName());
        return dto;
    }
}
