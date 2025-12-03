package com.bookspace.domain.wish.dao;

import com.bookspace.domain.wish.dto.WishResponseDto;
import com.bookspace.domain.wish.vo.WishVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WishDao {

    // 1. 찜 하기
    int insertWish(WishVo wishVo);

    // 2. 찜 해제
    int deleteWish(@Param("userId") long userId,
                   @Param("bookId") long bookId);

    // 3. 찜 목록 조회 (by userId) (도서 정보 포함)
    List<WishResponseDto> selectWishesByUserId(long userId);

    // 4. 유저가 찜한 책의 개수 (by userId)
    int countWishesByUserId(long userId);

    // 5. 책별 찜 개수 (by bookId)
    int countWishesByBookId(long bookId);

    // 6. 이미 찜을 했는지 확인
    boolean existsWish(@Param("userId") long userId,
                       @Param("bookId") long bookId);

}
