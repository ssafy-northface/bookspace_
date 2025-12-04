package com.bookspace.domain.book.dao;

import com.bookspace.domain.book.vo.BookVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookDao {
    // 책 검색
    List<BookVo> searchBooks(@Param("query") String query);

    // 책 존재 여부 - osbn
    boolean existsByIsbn (@Param("isbn") String isbn);

    // 책 정보 저장 (aladin api에서 조회한 데이터를 DB에 저장)
    int insertBOok(BookVo bookVo);
}
