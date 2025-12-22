package com.bookspace.domain.aiRecommendation.service;

import com.bookspace.domain.aiRecommendation.aiClient.OpenAiClient;
import com.bookspace.domain.aiRecommendation.dto.AiRecommendationRequestDto;
import com.bookspace.domain.aiRecommendation.dto.AiRecommendationResponseDto;
import com.bookspace.domain.aiRecommendation.service.AiRecommendationService;
import com.bookspace.domain.book.dto.BookDetailResponseDto;
import com.bookspace.domain.book.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AiRecommendationServiceImpl implements AiRecommendationService {

    @Autowired
    private OpenAiClient openAiClient;

    @Autowired
    private BookService bookService;

    @Override
    public AiRecommendationResponseDto getRecommendedBooks(AiRecommendationRequestDto requestDto) {

        String userRequest = requestDto.getUserRequest();

        // 0) 입력 방어
        if (userRequest == null || userRequest.isBlank()) {
            AiRecommendationResponseDto fail = new AiRecommendationResponseDto();
            fail.is_valid = false;
            fail.user_emotion = null;
            fail.comfort_message = "도서 추천을 받으려면 지금 감정/상황을 한 문장으로 적어주세요 🙂";
            fail.recommendations = List.of();
            fail.content = null;
            return fail;
        }

        // 1) OpenAI 호출: JSON 강제 결과를 DTO로 수신
        AiRecommendationResponseDto aiDto = openAiClient.requestJson(userRequest);

        // 2) 정책: ISBN 0개일 때만 실패 처리
        if (aiDto == null || aiDto.recommendations == null || aiDto.recommendations.isEmpty()) {
            AiRecommendationResponseDto fail = new AiRecommendationResponseDto();
            fail.is_valid = false;
            fail.user_emotion = null;
            fail.comfort_message =
                    "지금 마음에 맞는 책을 찾기 어려웠어요. 감정이나 상황을 조금만 더 구체적으로 말해줄래요? 🙂";
            fail.recommendations = List.of();
            fail.content = (aiDto != null ? aiDto.content : null);
            return fail;
        }

        // 3) ISBN 기반으로 도서 상세 조회하여 BookItem에 병합 (최대 3권)
        int limit = Math.min(3, aiDto.recommendations.size());
        List<AiRecommendationResponseDto.BookItem> mergedList = new ArrayList<>();

        for (int i = 0; i < limit; i++) {
            AiRecommendationResponseDto.BookItem aiItem = aiDto.recommendations.get(i);
            if (aiItem == null || aiItem.isbn13 == null || aiItem.isbn13.isBlank()) continue;

            BookDetailResponseDto detail = null;
            try {
                detail = bookService.getBookDetail(aiItem.isbn13);
            } catch (Exception e) {
                // 조회 실패 시에도 AI 추천(이유)은 살릴지 정책 선택 가능
                // 여기서는 "AI 추천은 유지 + 도서정보는 비움"으로 처리
            }

            AiRecommendationResponseDto.BookItem merged = new AiRecommendationResponseDto.BookItem();

            // ✅ GPT 제공값 유지
            merged.isbn13 = aiItem.isbn13;
            merged.theme = aiItem.theme;
            merged.description = aiItem.description;

            // ✅ 도서 정보 보강 (조회 성공 시)
            if (detail != null) {
                merged.bookId = detail.getBookId();
                merged.title = detail.getTitle();
                merged.author = detail.getAuthor();
                merged.publisher = detail.getPublisher();
                merged.cover = detail.getCover();
                merged.averageRating = detail.getAverageRating(); // DB 없으면 0.0 들어올 것
                // isbn은 detail.getIsbn()이 있을 수 있으니 정규화 원하면 아래처럼
                if (detail.getIsbn() != null && !detail.getIsbn().isBlank()) {
                    merged.isbn13 = detail.getIsbn();
                }
            } else {
                // 조회 실패면 최소한 null 처리(프론트 안정성)
                merged.bookId = null;
                merged.title = null;
                merged.author = null;
                merged.publisher = null;
                merged.cover = null;
                merged.averageRating = 0.0;
            }

            mergedList.add(merged);
        }

        // 4) 최종 반환: comfort_message + emotion은 aiDto 그대로, 추천 리스트만 교체
        aiDto.recommendations = mergedList;
        aiDto.is_valid = !mergedList.isEmpty(); // AI가 true라도 결과가 비면 false로

        return aiDto;
    }
}
