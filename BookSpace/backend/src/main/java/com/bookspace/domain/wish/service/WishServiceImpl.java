package com.bookspace.domain.wish.service;

import com.bookspace.domain.book.dao.BookDao;
import com.bookspace.domain.book.dto.AladinItemResponseDto;
import com.bookspace.domain.book.dto.AladinListResponseDto;
import com.bookspace.domain.book.external.AladinClient;
import com.bookspace.domain.book.vo.BookVo;
import com.bookspace.domain.wish.dao.WishDao;
import com.bookspace.domain.wish.dto.WishRequestDto;
import com.bookspace.domain.wish.dto.WishResponseDto;
import com.bookspace.domain.wish.vo.WishVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WishServiceImpl implements WishService {

    private final WishDao wishDao;
    private final BookDao bookDao;
    private final AladinClient aladinClient;

    // 1. 찜하기
    @Override
    @Transactional
    public void addWish(WishRequestDto wishRequestDto) {

        Long userId = wishRequestDto.getUserId();
        String isbn = wishRequestDto.getIsbn();

        if(isbn == null || isbn.isBlank()){
            throw new IllegalArgumentException("isbn is null or empty");
        }

        // isbn으로 DB 조회
        BookVo existing = bookDao.findBookByIsbn(isbn);

        Long bookId;
        // 이미 DB에 책이 있을 경우
        if(existing != null) {
            bookId = existing.getBookId();
        }

        // 없을 경우
        else{
            // 알라딘 api 호출
            AladinListResponseDto apiResponse = aladinClient.searchBooks(isbn,isbn,1);
            if(apiResponse == null || apiResponse.getItems()==null || apiResponse.getItems().isEmpty()){
                throw new IllegalArgumentException("해당 ISBN에 대한 도서 정보를 찾을 수 없습니다.");
            }
            AladinItemResponseDto item = apiResponse.getItems().get(0);  // 책 1권
            BookVo newBookVo = toBookVo(item); // DB에 저장할 VO 타입으로
            bookDao.insertBook(newBookVo);  // DB에 넣기
            bookId = newBookVo.getBookId(); // DB에 저장된 책 ID 반환
        }

        // wish 로직 처리 (userId + bookId)

        // 1) 존재하지 않는 bookId라면 예외 처리
        // if (!bookService.existsBook(bookId)) {
        //   throw new NotFoundException("존재하지 않는 책입니다.");
        //    }


        // 2) 이미 찜한 상태라면 예외 처리
        if (wishDao.existsWish(userId, bookId)) {
            throw new IllegalArgumentException("You have already wished this book.");
        }

        WishVo vo = new WishVo();
        vo.setUserId(userId);
        vo.setBookId(bookId);
        vo.setWishDate(LocalDateTime.now());

        int rows = wishDao.insertWish(vo);

        // DB에 insert 실패 시 처리
        if (rows != 1) {
            throw new IllegalStateException("Failed to add wish");
        }
    }


    // 2. 찜 해제
    @Override
    public void removeWish(long userId, long bookId) {

        // 1) 존재하지 않는 bookId라면 예외 처리
//        if (!bookService.existsBook(bookId)) {
//            throw new NotFoundException("Book not found.");
//        }

        int rows = wishDao.deleteWish(userId, bookId);

        // 찜하지 않았는데 해제하려한다면
        if (rows == 0) {
            throw new IllegalArgumentException("This book is not wished by the user.");
        }
    }

    // 3. 찜 목록 조회 (by userId)
    @Override
    public List<WishResponseDto> getWishesByUserId(long userId) {
        return wishDao.selectWishesByUserId(userId);
    }


    // 4. 책별 찜 개수
    @Override
    public int getWishCountByBookId(long bookId) {
        return wishDao.countWishesByBookId(bookId);
    }

    // 5. 유저가 찜한 책 개수
    @Override
    public int getWishCountByUserId(long userId) {
        return wishDao.countWishesByUserId(userId);
    }


    // --converter method-- 정리하기 (공통 로직으로)
    private BookVo toBookVo(AladinItemResponseDto item) {
        BookVo vo = new BookVo();
        vo.setBookTitle(item.getTitle());
        vo.setBookAuthor(item.getAuthor());
        vo.setBookPublisher(item.getPublisher());
        vo.setBookPublicationDate(item.getPubDate());
        vo.setBookIsbn(item.getIsbn13());
        vo.setBookDescription(item.getDescription());
        vo.setBookPrice(item.getPriceStandard());
        vo.setBookImageUrl(item.getCover());
        vo.setBookSalesPoint(item.getSalesPoint());
        vo.setBookCategory(item.getCategoryName());
        return vo;
    }

}
