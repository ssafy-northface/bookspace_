package com.bookspace.domain.book.vo;

import lombok.Data;

@Data
public class BookVo {
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookPublisher;
    private String bookPublicationDate;
    private String bookIsbn;
    private String bookDescription;
    private int bookPrice;
    private String bookImageUrl;
    private int bookSalesPoint;
    private String bookCategory;
}
