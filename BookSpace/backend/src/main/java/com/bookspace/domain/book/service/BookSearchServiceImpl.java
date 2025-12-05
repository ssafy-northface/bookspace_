package com.bookspace.domain.book.service;

import com.bookspace.domain.book.dto.AladinItemResponseDto;
import com.bookspace.domain.book.dto.AladinListResponseDto;
import com.bookspace.domain.book.dto.BookSearchResponseDto;
import com.bookspace.domain.book.external.AladinClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookSearchServiceImpl implements BookSearchService {

    private final AladinClient aladinClient;

    @Override
    public List<BookSearchResponseDto> searchBooks(String query, int size) {




        log.info("[BookSearchService] 검색어 = {}, size = {}", query, size);

        if(query == null || query.isBlank()){ // isBlank: 공백이 있어도 비어있다고 봄 <-> isEmpty
            return Collections.emptyList();
        }

        // 알라딘 api 호출
        AladinListResponseDto response = aladinClient.searchBooks(query,"isbn", size);

        if(response == null || response.getItems() == null){
            log.info("알라딘 응답이 없거나 item이 비어 있음");
            return Collections.emptyList();
        }

        // 알라딘 응답 -> 검색 response dto


        log.info("[BookSearchService] response.getItems() = {}", response.getItems());

        if (response.getItems() == null) {
            log.info("[BookSearchService] response.getItems() 가 null");
            return Collections.emptyList();
        }

        // ⭐ 여기까지 왔으면 이제 꼭 찍혀야 함
        log.info("[BookSearchService] 알라딘 item 개수 = {}", response.getItems().size());

        return response.getItems().stream()
                .map(this::toBookSearchResponseDto).toList();
    }

    // 알라딘 api에서 받아온 책을 하나씩 BookSearchResponseDto 타입으로 바꾸기 (stream)
    private BookSearchResponseDto toBookSearchResponseDto(AladinItemResponseDto item){
       BookSearchResponseDto bookSearchResponseDto = new BookSearchResponseDto();
        bookSearchResponseDto.setTitle(item.getTitle());
        bookSearchResponseDto.setAuthor(item.getAuthor());
        bookSearchResponseDto.setPublisher(item.getPublisher());
        bookSearchResponseDto.setPubDate(item.getPubDate());
        bookSearchResponseDto.setIsbn13(item.getIsbn13());
        bookSearchResponseDto.setCover(item.getCover());
        bookSearchResponseDto.setCategoryName(item.getCategoryName());

        return bookSearchResponseDto;

    }
}
