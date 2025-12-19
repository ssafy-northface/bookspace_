package com.bookspace.domain.book.service;

import com.bookspace.domain.book.dao.BookDao;
import com.bookspace.domain.book.dto.AladinItemResponseDto;
import com.bookspace.domain.book.dto.AladinListResponseDto;
import com.bookspace.domain.book.dto.BookDetailResponseDto;
import com.bookspace.domain.book.dto.BookSearchResponseDto;
import com.bookspace.domain.book.external.AladinClient;
import com.bookspace.domain.book.vo.BookVo;
import com.bookspace.domain.post.dao.PostDao;
import com.bookspace.domain.review.dao.ReviewDao;
import com.bookspace.domain.wish.dao.WishDao;
import com.bookspace.global.security.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bookspace.domain.book.converter.BookConverter;
import java.util.LinkedHashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookDao bookDao;
    private final AladinClient aladinClient;
    // 순환의존성 문제로 DAO를 직접 주입
    private final ReviewDao reviewDao;
    private final PostDao postDao;
    private final WishDao wishDao;


    // [검색] DB 검색 시 없으면 알라딘 API 호출
    @Override
    public List<BookSearchResponseDto> searchBooks(String query, String searchType) {

        // 1) DB 우선 검색
        List<BookSearchResponseDto> dbResult = searchBooksFromDb(query, searchType);
        if(!dbResult.isEmpty()){
            return dbResult;
        }
        // 없으면 API 호출
        return searchBooksFromAladin(query, searchType);
    }

    // [검색] DB
    private List<BookSearchResponseDto> searchBooksFromDb(String query, String searchType) {
        List<BookVo> dbResponse = bookDao.searchBooks(query, searchType);

        if(dbResponse==null||dbResponse.isEmpty()){
            return List.of();
        }

        return dbResponse.stream().map(BookConverter::toSearchResponseFromBookVo).toList(); // BookVo -> BookSearchResponseDto
    }

    // [검색] 알라딘 api
    private List<BookSearchResponseDto> searchBooksFromAladin(String query, String searchType) {
        AladinListResponseDto apiResponse = aladinClient.searchBooks(query, searchType, 10);

        if(apiResponse==null||apiResponse.getItems()==null){
            return List.of();
        }

        return apiResponse.getItems().stream().map(BookConverter::toSearchResponseFromAladinItem).toList();
    }


    /**
     * [조회] DB에서 isbn으로 bookId 조회 => return bookId
     */
    @Transactional
    public Long findBookIdByIsbnFromDb(String isbn){
        if(isbn == null||isbn.isBlank()){
            throw new IllegalArgumentException("isbn is null or empty.");
        }

        BookVo existing = bookDao.findBookByIsbn(isbn);
        return (existing == null) ? null : existing.getBookId();
    }


    /**
     * [조회 & 저장] isbn 값으로 알라딘 api 호출 후, 해당 isbn 값을 가진 도서를 DB에 저장 후 bookId 반환 (저장용)
     * !! 사용하기 전 DB에 책이 없는지 확인해야 함!! (protected)
     */
    @Transactional
    protected Long fetchAndSaveBookByIsbnFromAladin(String isbn){
        AladinListResponseDto apiResponse = aladinClient.searchBooks(isbn, "isbn",1);

        if(apiResponse==null || apiResponse.getItems()==null){
            throw new IllegalArgumentException("Book Not Found for ISBN:"+isbn);
        }

        AladinItemResponseDto item = apiResponse.getItems().get(0);

        BookVo newBookVo = BookConverter.toBookVoFromAladinItem(item); // 알라딘 응답 -> BookVo
        bookDao.insertBook(newBookVo);

        return newBookVo.getBookId();
    }

    /**
     * [유저 액션용]: 책 찾기 + (없으면 저장) => return bookId
     * DB에서 isbn에 해당하는 책 조회 후  (findBookByIsbn)
     * 없으면 알라딘 api 호출후 DB에 해당 책 저장 (fetchAndSaveBookByIsbnFromAladin)
      */
    @Override
    @Transactional
    public Long ensureBookExists(String isbn) {
        if(isbn == null || isbn.isBlank()){
            throw new IllegalArgumentException("isbn is null or empty");
        }
        // DB에서 isbn 조회
        Long existingBookId = findBookIdByIsbnFromDb(isbn);
        if(existingBookId!=null){
            return existingBookId;
        }

        // DB에 없는 경우 알라딘 api 호출 후 DB에 책 저장
        return fetchAndSaveBookByIsbnFromAladin(isbn);
    }


    /**
     * 알라딘 API 도서 검색
     */
    public List<BookSearchResponseDto> searchBooksFromAladin(
            String query,
            String searchType,
            String sort
    ) {
        final int maxResults = 100;

        // 1. 알라딘 API 호출
        AladinListResponseDto apiResponse =
                aladinClient.searchBooks(query, searchType, sort, maxResults);

        if (apiResponse == null || apiResponse.getItems() == null) {
            return List.of();
        }

        // 2. 검색어 정규화
        String normalizedQuery = normalizeQuery(query);
        if (normalizedQuery.isBlank()) {
            return List.of();
        }

        // 3. 점수 계산 → 정렬 → 응답 변환
        return apiResponse.getItems().stream()
                .map(item -> scoreItem(item, searchType, normalizedQuery))
                .filter(scored -> scored.score > 0)
                .sorted((a, b) -> Integer.compare(b.score, a.score))
                .map(scored ->
                        BookConverter.toSearchResponseFromAladinItem(scored.item))
                .toList();
    }

    /**
     * 개별 아이템 점수 계산
     */
    private ScoredItem scoreItem(
            AladinItemResponseDto item,
            String searchType,
            String query
    ) {
        String rawTarget = extractTarget(item, searchType);
        String normalizedTarget = normalizeTarget(rawTarget);

        int score = SearchScorer.score(query, normalizedTarget);
        return new ScoredItem(item, score);
    }

    /**
     * 검색 타입별 비교 대상 추출
     */
    private String extractTarget(AladinItemResponseDto item, String searchType) {
        return switch (searchType.toLowerCase()) {
            case "author" -> item.getAuthor();
            case "publisher" -> item.getPublisher();
            default -> item.getTitle();
        };
    }


    /**
     * 검색어 정규화 (공백 유지)
     */
    private String normalizeQuery(String raw) {
        if (raw == null) return "";
        return raw.trim()
                .toLowerCase()
                .replaceAll("\\s+", " ");
    }

    /**
     * 검색 대상 정규화 (title, author 등)
     */
    private String normalizeTarget(String raw) {
        if (raw == null) return "";

        String s = raw.toLowerCase();

        // 괄호/대괄호 제거 (부제, 개정판 등)
        s = s.replaceAll("\\(.*?\\)", " ");
        s = s.replaceAll("\\[.*?\\]", " ");

        // 특수문자 제거
        s = s.replaceAll("[^\\p{IsHangul}\\p{IsAlphabetic}\\p{IsDigit}\\s]", " ");

        return s.replaceAll("\\s+", " ").trim();
    }


    /**
     * 검색 점수 계산기
     *
     * - 기본 비교 + 띄어쓰기 무시 비교
     * - 필터링 x / 랭킹
     */
    private static class SearchScorer {

        static int score(String query, String target) {
            if (query.isBlank() || target.isBlank()) return 0;

            int score = 0;

            //  기본 비교

            if (target.equals(query)) score += 100;
            if (target.startsWith(query)) score += 50;
            if (target.startsWith(query + "의")) score += 45;
            if (target.contains(query)) score += 20;

            // 띄어쓰기 무시 비교

            String noSpaceQuery  = query.replace(" ", "");
            String noSpaceTarget = target.replace(" ", "");

            if (noSpaceTarget.equals(noSpaceQuery)) score += 80;
            if (noSpaceTarget.startsWith(noSpaceQuery)) score += 40;
            if (noSpaceTarget.contains(noSpaceQuery)) score += 15;

            return score;
        }
    }

    /**
     * 점수 포함 래퍼
     */
    private static class ScoredItem {
        private final AladinItemResponseDto item;
        private final int score;

        private ScoredItem(AladinItemResponseDto item, int score) {
            this.item = item;
            this.score = score;
        }
    }




    /**
     * [기본 목록] 검색어 없이 알라딘에서 기본 도서 리스트 조회
     * - 메인 도서 목록 화면에서 사용
     * - type: new | bestseller
     */
    @Override
    public List<BookSearchResponseDto> getDefaultBooksFromAladin(String type) {
        // type: "new" | "bestseller"
        AladinListResponseDto apiResponse =
                aladinClient.fetchDefaultBooks(type, 100);

        if (apiResponse == null || apiResponse.getItems() == null) {
            return List.of();
        }

        return apiResponse.getItems().stream()
                .map(BookConverter::toSearchResponseFromAladinItem)
                .toList();
    }

    /**
     * [상세조회용 - 저장 X] ISBN으로 알라딘 API 단건 조회
     * - DB 저장 없이 조회 결과(단건 item)만 반환
     * - 내부적으로 ItemSearch + QueryType=ISBN + MaxResults=1 사용
     */
    private AladinItemResponseDto fetchBookByIsbnFromAladinNoSave(String isbn) {
        AladinListResponseDto apiResponse = aladinClient.searchBooks(isbn, "isbn", 1);

        if (apiResponse == null || apiResponse.getItems() == null || apiResponse.getItems().isEmpty()) {
            throw new IllegalArgumentException("Book Not Found for ISBN: " + isbn);
        }

        return apiResponse.getItems().get(0);
    }


    // 도서 상세조회 시
    // DB에 있는 책 -> DB에서 조회
    // DB에 없는 책 -> 알라딘 API 호출 (저장 X, 단순 조회)
    @Override
    public BookDetailResponseDto getBookDetail(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            throw new IllegalArgumentException("isbn is null or empty");
        }

        // 1) DB 우선 조회
        BookVo existing = bookDao.findBookByIsbn(isbn);

        if (existing != null) {
            BookDetailResponseDto dto = BookConverter.toDetailResponseFromBookVo(existing);

            Long bookId = existing.getBookId();

            // 메타값
            Double avgRating = reviewDao.selectAverageRating(bookId);
            Integer reviewCnt = reviewDao.countReviews(bookId);
            Integer postCnt = postDao.countPostsByBookId(bookId);

            // 메타 기본값 (다음 단계에서 review/post/wish 연결)
            dto.setAverageRating(avgRating == null ? 0.0 : avgRating);
            dto.setReviewCount(reviewCnt == null ? 0 : reviewCnt);
            dto.setPostCount(postCnt == null ? 0 : postCnt);
            dto.setIsWished(isWishedByCurrentUser(bookId));

            return dto;
        }

        // 2) DB에 없으면 알라딘 호출(저장 X)
        AladinItemResponseDto item = fetchBookByIsbnFromAladinNoSave(isbn);
        BookDetailResponseDto dto = BookConverter.toDetailResponseFromAladinItem(item);

        // DB에 없는 책이면 메타 기본값
        dto.setAverageRating(0.0);
        dto.setReviewCount(0);
        dto.setPostCount(0);
        dto.setIsWished(false);

        return dto;
    }

    private boolean isWishedByCurrentUser(Long bookId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;

        Object principal = auth.getPrincipal();
        if (!(principal instanceof CustomUserDetails userDetails)) return false;

        long userId = userDetails.getUserId();
        return wishDao.existsWish(userId, bookId) > 0;
    }


}
