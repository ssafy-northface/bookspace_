package com.bookspace.domain.wish.dto;


import lombok.Data;

@Data
public class WishRequestDto {

    private long userId;
    private String isbn;

}