package com.bookspace.domain.wish.service;

import com.bookspace.domain.book.service.BookService;
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
    private final BookService bookService;

    // 1. 찜하기
    @Override
    @Transactional
    public void addWish(WishRequestDto wishRequestDto) {

        Long userId = wishRequestDto.getUserId();
        String isbn = wishRequestDto.getIsbn();

        if(isbn == null || isbn.isBlank()){
            throw new IllegalArgumentException("isbn is null or empty");
        }

        // 1) isbn으로 DB 조회 (없으면 알라딘 api 호출해서 DB 저장) => bookId 받아오기
       Long bookId = bookService.ensureBookExists(isbn);

        // wish 로직 처리 (userId + bookId)
        // 1) 존재하지 않는 bookId라면 예외 처리
        // if (!bookService.existsBook(bookId)) {
        //   throw new NotFoundException("존재하지 않는 책입니다.");
        //    }


        // 2) 이미 찜한 상태라면 예외 처리
        if (wishDao.existsWish(userId, bookId)) {
            throw new IllegalArgumentException("You have already wished this book.");
        }

        // 3) 찜 저장
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

}
