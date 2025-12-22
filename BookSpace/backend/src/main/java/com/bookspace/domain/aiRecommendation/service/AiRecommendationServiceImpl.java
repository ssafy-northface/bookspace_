package com.bookspace.domain.aiRecommendation.service.impl;

import com.bookspace.domain.aiRecommendation.dto.AiRecommendationRequestDto;
import com.bookspace.domain.aiRecommendation.dto.AiRecommendationResponseDto;
import com.bookspace.domain.aiRecommendation.service.AiRecommendationService;
import com.bookspace.domain.aiRecommendation.aiClient.OpenAiClient;
import com.bookspace.domain.book.service.BookService;  // DB에서 도서 조회하는 서비스
import com.bookspace.domain.book.model.Book;  // Book 모델
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AiRecommendationServiceImpl implements AiRecommendationService {

    @Autowired
    private OpenAiClient openAiClient;  // AI 모델과의 연동을 처리하는 클라이언트

    @Autowired
    private BookService bookService;  // DB 서비스 (도서 조회)

    @Override
    public AiRecommendationResponseDto getRecommendedBooks(AiRecommendationRequestDto requestDto) {

        // 1. 사용자의 요청을 받아 GPT 모델로부터 ISBN13을 받는다
        String userRequest = requestDto.getUserRequest();
        String isbn13 = openAiClient.request(userRequest);  // OpenAI로부터 ISBN13을 받음

        // 2. DB에서 ISBN13을 통해 도서 정보 확인
        Book recommendedBook = bookService.findBookByIsbn(isbn13);  // DB에서 도서 검색

        // 3. DB에 도서가 없으면 알라딘 API 호출
        if (recommendedBook == null) {
            recommendedBook = bookService.fetchBookFromAladin(isbn13);  // 알라딘 API 호출
        }

        // 4. 추천 도서 정보를 응답에 담아 반환
        AiRecommendationResponseDto responseDto = new AiRecommendationResponseDto();
        responseDto.setRecommendedBooks(List.of(recommendedBook.getTitle()));  // 추천된 도서 리스트

        return responseDto;
    }
}
