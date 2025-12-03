package com.bookspace.domain.wish.service;

import com.bookspace.domain.wish.dto.WishResponseDto;
import com.bookspace.domain.wish.vo.WishVo;

import java.util.List;

public interface WishService {

    // 1. 찜 하기
    void addWish(long userId, long bookId);

    // 2. 찜 해제
    void removeWish(long userId, long bookId);

    // 3. 찜한 책 목록 조회 (by userId)
    List<WishResponseDto> getWishesByUserId(long userId);

    // 4. 책별 찜 개수
    int getWishCountByBookId(long bookId);

    // 5. 유저가 찜한 책 개수
    int getWishCountByUserId(long userId);

}
