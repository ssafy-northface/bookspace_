package com.bookspace.domain.book.converter;

import com.bookspace.domain.book.dto.AladinItemResponseDto;
import com.bookspace.domain.book.dto.BookDetailResponseDto;
import com.bookspace.domain.book.dto.BookSearchResponseDto;
import com.bookspace.domain.book.vo.BookVo;


public final class BookConverter {

    // 유틸 클래스 -> 인스턴스 생성 방지
    private BookConverter() {}

    /**
     * 알라딘 API 응답 -> DB 저장용 BookVo
     */
    public static BookVo toBookVoFromAladinItem(AladinItemResponseDto item) {
        if (item == null) return null;

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

    /**
     * DB에서 조회한 BookVo -> 검색 응답 DTO
     */
    public static BookSearchResponseDto toSearchResponseFromBookVo(BookVo vo) {
        if (vo == null) return null;

        BookSearchResponseDto dto = new BookSearchResponseDto();
        dto.setTitle(vo.getBookTitle());
        dto.setAuthor(vo.getBookAuthor());
        dto.setPublisher(vo.getBookPublisher());
        dto.setPubDate(vo.getBookPublicationDate());
        dto.setIsbn13(vo.getBookIsbn());
        dto.setCover(vo.getBookImageUrl());
        dto.setCategoryName(vo.getBookCategory());
        dto.setDescription(vo.getBookDescription());
        return dto;
    }

    /**
     * 알라딘 검색 결과 DTO -> 검색 응답 DTO
     * (DB에 저장하지 않고 바로 검색 결과로 내려줄 때)
     */
    public static BookSearchResponseDto toSearchResponseFromAladinItem(AladinItemResponseDto item) {
        if (item == null) return null;

        BookSearchResponseDto dto = new BookSearchResponseDto();
        dto.setTitle(item.getTitle());
        dto.setAuthor(item.getAuthor());
        dto.setPublisher(item.getPublisher());
        dto.setPubDate(item.getPubDate());
        dto.setIsbn13(item.getIsbn13());
        dto.setCover(item.getCover());
        dto.setCategoryName(item.getCategoryName());
        dto.setDescription(item.getDescription());
        return dto;
    }


    // DB에서 조회 -> Detail DTO
    public static BookDetailResponseDto toDetailResponseFromBookVo(BookVo vo) {
        if (vo == null) return null;

        BookDetailResponseDto dto = new BookDetailResponseDto();
        dto.setBookId(vo.getBookId());
        dto.setIsbn(vo.getBookIsbn());
        dto.setTitle(vo.getBookTitle());
        dto.setAuthor(vo.getBookAuthor());
        dto.setPublisher(vo.getBookPublisher());
        dto.setPubDate(vo.getBookPublicationDate());
        dto.setCover(vo.getBookImageUrl());
        dto.setDescription(vo.getBookDescription());

        return dto;
    }

    // 알라딘 API 조회 -> Detail DTO
    public static BookDetailResponseDto toDetailResponseFromAladinItem(
            AladinItemResponseDto item
    ) {
        if (item == null) return null;

        BookDetailResponseDto dto = new BookDetailResponseDto();
        dto.setIsbn(item.getIsbn13());   // 또는 item.getIsbn() → 실제 필드명만 확인
        dto.setTitle(item.getTitle());
        dto.setAuthor(item.getAuthor());
        dto.setPublisher(item.getPublisher());
        dto.setPubDate(item.getPubDate());
        dto.setCover(item.getCover());
        dto.setDescription(item.getDescription());

        return dto;
    }
}
