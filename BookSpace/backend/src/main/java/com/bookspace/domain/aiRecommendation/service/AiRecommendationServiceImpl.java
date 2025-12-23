package com.bookspace.domain.aiRecommendation.service;

import com.bookspace.domain.aiRecommendation.aiClient.OpenAiClient;
import com.bookspace.domain.aiRecommendation.dto.AiRecommendationResponseDto;
import com.bookspace.domain.aiRecommendation.dto.ApiRequestDto;
import com.bookspace.domain.aiRecommendation.dto.ApiResponseDto;
import com.bookspace.domain.aiRecommendation.service.AiRecommendationService;
import com.bookspace.domain.book.dto.BookDetailResponseDto;
import com.bookspace.domain.book.dto.BookSearchResponseDto;
import com.bookspace.domain.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiRecommendationServiceImpl implements AiRecommendationService {

    private final OpenAiClient openAiClient;
    private final BookService bookService;

    @Override
    public AiRecommendationResponseDto getRecommendedBooks(ApiRequestDto requestDto) {

        String userRequest = requestDto != null ? requestDto.getUserRequest() : null;

        // 0) 입력 방어
        if (userRequest == null || userRequest.isBlank()) {
            return buildFailResponse(
                    "도서 추천을 받으려면 지금 감정이나 상황을 한 문장으로 적어주세요 🙂"
            );
        }

        // 1) OpenAI 호출 (제목 3개 + 감정/위로 메시지)
        ApiResponseDto apiDto = openAiClient.requestJson(userRequest);

        if (apiDto == null || apiDto.book_titles == null || apiDto.book_titles.isEmpty()) {
            return buildFailResponse(
                    "지금 마음에 맞는 책을 찾기 어려웠어요. 감정이나 상황을 조금만 더 구체적으로 말해줄래요? 🙂"
            );
        }

        // 2) 제목 → 검색 → isbn13 → 상세 조회
        List<AiRecommendationResponseDto.BookItem> items = new ArrayList<>();

        for (String title : apiDto.book_titles) {
            if (title == null || title.isBlank()) continue;

            String isbn13 = resolveIsbn13ByTitle(title);
            if (isbn13 == null) continue;

            BookDetailResponseDto detail;
            try {
                detail = bookService.getBookDetail(isbn13);
            } catch (Exception e) {
                continue;
            }

            AiRecommendationResponseDto.BookItem item =
                    new AiRecommendationResponseDto.BookItem();

            item.setBookId(detail.getBookId());                // ⭐ Long (없으면 null)
            item.setIsbn13(detail.getIsbn());
            item.setTitle(detail.getTitle());
            item.setAuthor(detail.getAuthor());
            item.setPublisher(detail.getPublisher());
            item.setCover(detail.getCover());
            item.setAverageRating(
                    detail.getAverageRating() != null ? detail.getAverageRating() : 0.0
            );

            items.add(item);
            if (items.size() == 3) break;
        }

        // 3) 최종 응답
        AiRecommendationResponseDto res = new AiRecommendationResponseDto();
        res.setUserEmotion(apiDto.user_emotion);
        res.setComfortMessage(apiDto.comfort_message);
        res.setRecommendations(items);

        if (items.isEmpty()) {
            res.setValid(false);
            res.setUserEmotion(null);
            res.setComfortMessage(
                    "추천 도서 제목은 받았지만 실제 도서를 찾지 못했어요. 감정이나 상황을 조금만 더 구체적으로 말해줄래요? 🙂"
            );
        } else {
            res.setValid(true);
        }

        return res;
    }

    /**
     * GPT 제목 → DB/알라딘 검색 → 가장 적합한 isbn13 1개 반환
     */
    private String resolveIsbn13ByTitle(String title) {
        String normalized = normalizeTitle(title);

        List<BookSearchResponseDto> results;
        try {
            results = bookService.searchBooks(normalized, "title");
        } catch (Exception e) {
            return null;
        }

        if (results == null || results.isEmpty()) return null;
        return results.get(0).getIsbn13();
    }

    private String normalizeTitle(String raw) {
        if (raw == null) return "";
        return raw.replaceAll("\\(.*?\\)|\\[.*?\\]", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private AiRecommendationResponseDto buildFailResponse(String message) {
        AiRecommendationResponseDto fail = new AiRecommendationResponseDto();
        fail.setValid(false);
        fail.setUserEmotion(null);
        fail.setComfortMessage(message);
        fail.setRecommendations(List.of());
        return fail;
    }
}
