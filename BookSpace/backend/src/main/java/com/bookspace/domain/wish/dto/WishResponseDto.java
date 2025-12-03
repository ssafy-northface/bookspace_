package com.bookspace.domain.wish.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WishResponseDto {

    private long wishId;
    private long userId;
    private long bookId;
    private LocalDateTime wishDate;

    // 찜 목록 조회 시 필요한 book 정보
    private String bookTitle;
    private String bookAuthor;
    private String bookImageUrl;
}