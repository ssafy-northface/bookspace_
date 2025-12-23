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

        if (apiDto == null || apiDto.books == null || apiDto.books.isEmpty()) {
            return buildFailResponse(
                    "지금 마음에 맞는 책을 찾기 어려웠어요. 감정이나 상황을 조금만 더 구체적으로 말해줄래요? 🙂"
            );
        }

        // 2) 제목 → 검색 → isbn13 → 상세 조회
        List<AiRecommendationResponseDto.BookItem> items = new ArrayList<>();

        for (ApiResponseDto.AiRecommendationItemDto b : apiDto.books) {
            if (b == null) continue;

            String title = b.title;
            String reason = b.reason;

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

            item.setBookId(detail.getBookId());
            item.setIsbn13(detail.getIsbn());
            item.setTitle(detail.getTitle());
            item.setAuthor(detail.getAuthor());
            item.setPublisher(detail.getPublisher());
            item.setCover(detail.getCover());
            item.setAverageRating(
                    detail.getAverageRating() != null ? detail.getAverageRating() : 0.0
            );
            item.setReason(reason != null ? reason.trim() : null);
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

        BookSearchResponseDto best = pickBestByTitle(normalized, results);

        return best != null ? best.getIsbn13() : null;
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

    private BookSearchResponseDto pickBestByTitle(String normalizedQuery, List<BookSearchResponseDto> results) {
        BookSearchResponseDto best = null;
        int bestScore = Integer.MIN_VALUE;

        for (BookSearchResponseDto r : results) {
            if (r == null || r.getTitle() == null || r.getTitle().isBlank()) continue;

            String normalizedTitle = normalizeTitle(r.getTitle());
            if (normalizedTitle.isBlank()) continue;

            int score = scoreTitleMatch(normalizedQuery, normalizedTitle);

            if (score > bestScore) {
                bestScore = score;
                best = r;
            }
        }

        // 너무 애매하게 매칭되면 null로 처리해서 스킵하도록(선택)
        // if (bestScore < 40) return null;

        return best;
    }

    private int scoreTitleMatch(String q, String t) {
        // 1) 완전일치
        if (t.equals(q)) return 1000;

        // 2) 접두어 일치(표기/공백 차이 대응)
        if (t.startsWith(q) || q.startsWith(t)) return 900;

        // 3) 포함 관계
        if (t.contains(q) || q.contains(t)) return 800;

        // 4) 토큰(단어) 겹침 점수
        List<String> qTokens = tokenize(q);
        List<String> tTokens = tokenize(t);

        int intersect = 0;
        for (String tok : qTokens) {
            if (tTokens.contains(tok)) intersect++;
        }

        int union = qTokens.size() + tTokens.size() - intersect;
        double jaccard = union == 0 ? 0 : (double) intersect / union;

        int lenPenalty = Math.abs(q.length() - t.length());
        int tokenScore = (int) Math.round(jaccard * 100);

        return tokenScore * 5 - lenPenalty; // 가중치는 필요하면 조절
    }

    private List<String> tokenize(String s) {
        if (s == null) return List.of();
        String[] parts = s.split("\\s+");

        List<String> tokens = new ArrayList<>();
        for (String p : parts) {
            if (p == null || p.isBlank()) continue;
            if (p.length() <= 1) continue; // 1글자 토큰 제거(노이즈 감소)
            tokens.add(p);
        }
        return tokens;
    }
}
