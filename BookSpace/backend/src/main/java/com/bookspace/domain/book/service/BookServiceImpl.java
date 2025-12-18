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
import lombok.RequiredArgsConstructor;
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

    // 알라딘 API로만 책 조회 (3개의 파라미터)
    // searchType에 따라 알라딘 api 조회 결과 부정확함
    // -> 서버에서 searchType별로
    // query와 일치하는 도서 -> query로 시작하는 도서의 순으로 최대 3권 반환
    @Override
    public List<BookSearchResponseDto> searchBooksFromAladin(String query, String searchType,   String sort) {

        final int maxResults = 100;

        AladinListResponseDto apiResponse =
                aladinClient.searchBooks(query, searchType, sort, maxResults);

        if (apiResponse == null || apiResponse.getItems() == null) {
            return List.of();
        }

        String normalizedQuery = normalize(query);
        if (normalizedQuery.isEmpty()) {
            return List.of();
        }

        List<AladinItemResponseDto> items = apiResponse.getItems();

        // 중복 제거 + 순서 유지 (equals 결과 먼저, 그 다음 startsWith 결과)
        LinkedHashSet<AladinItemResponseDto> result = new LinkedHashSet<>();

        // 1) equals 우선
        for (AladinItemResponseDto item : items) {
            String target = normalize(extractTarget(item, searchType));
            if (!target.isEmpty() && target.equals(normalizedQuery)) {
                result.add(item);
            }
        }

        // 2) startsWith
        for (AladinItemResponseDto item : items) {
            String target = normalize(extractTarget(item, searchType));
            if (!target.isEmpty() && target.startsWith(normalizedQuery)) {
                result.add(item);
            }
        }

        return result.stream()
                .map(BookConverter::toSearchResponseFromAladinItem)
                .toList();
    }

    /**
     * 비교용 정규화:
     * - 괄호/대괄호 내용 제거: (개정판), [세트] 등
     * - 특수문자 제거
     * - 공백 정리
     * - 소문자 변환
     */
    private String normalize(String raw) {
        if (raw == null) return "";

        String s = raw.trim().toLowerCase();

        // () [] 안 내용 제거
        s = s.replaceAll("\\(.*?\\)", " ");
        s = s.replaceAll("\\[.*?\\]", " ");

        // 한글/영문/숫자/공백만 남김
        s = s.replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}\\p{IsHangul}\\s]", " ");

        // 공백 정규화
        return s.replaceAll("\\s+", " ").trim();
    }


    /**
     * 검색 타입에 따라 비교 대상 문자열 추출
     */
    private String extractTarget(AladinItemResponseDto item, String searchType) {
        String raw =  switch(searchType.toLowerCase()){
            case "title" -> item.getTitle();
            case "author" -> item.getAuthor();
            case "publisher" -> item.getPublisher();
            default -> null;
        };

        if (raw == null) return null;

        int idx = raw.indexOf("(");
        if (idx > 0) {
            raw = raw.substring(0, idx);
        }

        return raw.trim();
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
            dto.setIsWished(false);

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



}
