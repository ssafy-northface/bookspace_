package com.bookspace.domain.book.dao;

import com.bookspace.domain.book.vo.BookVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookDao {
    /**
     * /books/search?query=쿼리 검색 시
     * DB에서 먼저 해당 검색어가 포함된 책들을 조회하는 메서드 -> 결과가 비어있으면 Aladin API 호출
     * @param query
     */
    List<BookVo> searchBooks(@Param("query") String query, @Param("searchType") String searchType);

    /**
     *  isbn을 통한 DB 책 존재 여부 확인 및 해당 책의 book_id 반환
     *  - 책의 존재 여부만으로 유저 액션 처리 불가 (book_id를 알아야 wish 로직 등 처리 가능)
     *  - isbn으로 DB에 이미 저장되어 있는지 확인하는 메서드
     *  유저가 찜, 리뷰, 포스트 작성할 때 해당 책이 있는지 isbn을 통해 DB에서 조회 (종복 저장 방지) -> 해당 책의 book_id를 반환
     * @param isbn
     */
    BookVo findBookByIsbn (@Param("isbn") String isbn);

    /** 책 정보 저장
     * DB에 첵이 없을 때 aladin api에서 조회한 데이터를 DB에 저장
     * @param bookVo
     */
    int insertBook(BookVo bookVo);



}
