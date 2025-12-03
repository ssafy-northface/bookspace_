package com.bookspace.domain.wish.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WishVo {
        private long wishId;
        private long userId;
        private long bookId;
        private LocalDateTime wishDate;
}
