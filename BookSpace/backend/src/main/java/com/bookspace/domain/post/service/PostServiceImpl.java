package com.bookspace.domain.post.service;

import java.util.HashMap;
import java.util.List;

import com.bookspace.domain.book.service.BookService;
import com.bookspace.domain.post.dto.PostPageResponseDto;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookspace.domain.post.dao.PostDao;
import com.bookspace.domain.post.dto.PostRequestDto;
import com.bookspace.domain.post.dto.PostResponseDto;
import com.bookspace.domain.post.vo.PostVo;
import java.util.Map;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {

    private final PostDao postDao;
    private final BookService bookService;

    // 게시글 작성 (isbn)
    @Override
    public void createPost(PostRequestDto dto, long loginUserId) {

//        Long userId = dto.getUserId();
        String isbn = dto.getIsbn();

//        if(userId == null){
//            throw new IllegalArgumentException("userId is null");
//        }

        if(isbn == null){
            throw new IllegalArgumentException("isbn is null");
        }

        // 책 isbn 기준으로 DB에 저장이 되어 있어야 함 -> bookId
        Long bookId = bookService.ensureBookExists(isbn);
        /*
        ensureBookExists 내부 로직
        - FindBookIdByIsbnFromDb(isbn) 으로 DB 조회
        - 없으면 fetchAndSaveBookByIsbnFromAladin(isbn) 호출해서 저장 후 bookId 반환
         */

        PostVo vo = new PostVo();
        vo.setUserId(loginUserId);
        vo.setBookId(bookId);
        vo.setPostTitle(dto.getPostTitle());
        vo.setPostContent(dto.getPostContent());

        int rows = postDao.insertPost(vo);
        if(rows != 1){
            throw new IllegalArgumentException("Failed to insert post");
        }
    }


    /*
    모든 게시글 조회 (isbn numm) & 게시글 검색 (isbn) - 페이지네이션
    모든 게시물 조회는 게시물 좋아요 개수, 좋아요 여부 2개가 모두 포함된 응답이 옴
    - 로그인한 유저라면, 자신이 게시물을 좋아요 눌렀는지 여부 (full heart / empty heart)
    - 로그인한 유저라면, liked값이 모두 false로 옴 -> 프론트 처리 (empty heart를 유저가 누르려고 시도했을 때 로그인 하라고 하기)
     */
    @Override
    public PostPageResponseDto getAllPosts(int page, int size, String isbn,String sort, Long userId)
    {
        int safePage = Math.max(page,0);
        int safeSize = Math.min(Math.max(size,0),30);
        int offset = page * size;

        String trimmedIsbn = (isbn == null || isbn.isBlank()) ? null : isbn.trim();

        String normalizedSort = (sort == null) ? "lastest":sort.trim().toLowerCase();
        if(!normalizedSort.equals("latest")&& !normalizedSort.equals("comments")){
            normalizedSort = "latest";

        }

        Map<String, Object> params = new HashMap<>();
        params.put("size", size);
        params.put("offset", offset);
        params.put("userId", userId);
        params.put("isbn", trimmedIsbn); // isbn이 있으면 넣기
        params.put("sort", normalizedSort);

        List<PostResponseDto> posts = postDao.selectAllPosts(params);

        int totalCount = postDao.countAllPosts(trimmedIsbn);

        boolean hasNext = (page + 1) * size < totalCount;
        Integer nextPage = hasNext ? page + 1 : null;


        PostPageResponseDto.Content content =
                new PostPageResponseDto.Content(posts, page, size, totalCount);

        return new PostPageResponseDto(content, hasNext, nextPage);


    }

    @Override
    @Transactional
    public PostResponseDto getPostById(long postId, Long userId) {

        PostResponseDto responseDto = postDao.selectPostById(postId,userId);

        if(responseDto == null){
            throw new IllegalArgumentException("Post not found with id: " + postId);
        }

        postDao.increaseViewCount(postId);
        return responseDto;
    }

    @Override
    public void updatePost(long postId, PostRequestDto dto, long loginUserId) {
//        PostVo vo = new PostVo();
//        vo.setPostId(postId);
//        vo.setPostTitle(dto.getPostTitle());
//        vo.setPostContent(dto.getPostContent());
//        int updatedRows = postDao.updatePost(vo);
//        if (updatedRows == 0) {
//            // 존재하지 않는 postId일 때
//            throw new IllegalArgumentException("Post not found with id: " + postId);
//        }

        // 1. 기존 게시글 조회
        PostResponseDto responseDto = postDao.selectPostById(postId,loginUserId);
        if(responseDto == null){
            throw new IllegalArgumentException("Post not found with id: " + postId);
        }

        // 2. 작성자 userId 기준으로 권한 체크
        if(responseDto.getUserId() != loginUserId){
            throw new AccessDeniedException("Access denied 본인의 게시글만 수정할 수 있습니다");
        }

        // 3. 통과하면 수정 진행
        PostVo vo = new PostVo();
        vo.setPostId(postId);
        vo.setPostTitle(dto.getPostTitle());
        vo.setPostContent(dto.getPostContent());

        int updatedRows = postDao.updatePost(vo);
        if(updatedRows != 1){
            throw new RuntimeException("Failed to update post: " + postId);
        }



    }


    @Override
    public void deletePost(long postId, Long loginUserId) {
//        int deletedRows = postDao.deletePost(postId);
//        if (deletedRows == 0) {
//            throw new IllegalArgumentException("Post not found with id: " + postId);
//        }

        // 1. 기존 게시글 조회
       PostResponseDto responseDto= postDao.selectPostById(postId, loginUserId);
        if(responseDto == null){
            throw new IllegalArgumentException("Post not found with id: " + postId);
        }


        // 2. 작성자 체크
        if(responseDto.getUserId() != loginUserId){
            throw new AccessDeniedException("Access denied 본인의 게시글만 삭제할 수 있습니다.");
        }

        // 3. 통과하면 삭제
        int deletedRows = postDao.deletePost(postId);
        if(deletedRows != 1){
            throw new RuntimeException("Failed to delete post: " + postId);
        }

    }


    @Override
    public void increaseViewCount(long postId) {
        postDao.increaseViewCount(postId);
    }


    @Override
    public List<PostResponseDto> getPostsByBookId(long bookId) {
        List<PostVo> postVoList = postDao.selectPostsByBookId(bookId);
        return postVoList.stream()
                .map(this::convertToResponseDto)
                .toList();
    }

    @Override
    public List<PostResponseDto> getPostsByUserId(long userId) {
        List<PostResponseDto> results = postDao.selectPostsByUserId(userId);
        return results;
    }

    @Override
    public List<PostResponseDto> getPostsByKeyword(String keyword) {
        List<PostVo> postVoList = postDao.selectPostsByKeyword(keyword);
        return postVoList.stream()
                .map(this::convertToResponseDto)
                .toList();
    }

    @Override
    public List<PostResponseDto> getLikedPostsByUserId(Long userId) {
        if(userId == null){
            throw new IllegalArgumentException("userId is null (login required)");
        }

        List<PostVo> postVoList = postDao.selectLikedPostsByUserId(userId);

        return postVoList.stream()
                .map(this::convertToResponseDto)
                .toList();
    }

    @Override
    public Integer getPostCountByBookId(long bookId) {
        return postDao.countPostsByBookId(bookId);
    }



    private PostResponseDto convertToResponseDto(PostVo post) {
        if (post == null) return null;

        PostResponseDto dto = new PostResponseDto();
        dto.setPostId(post.getPostId());
        dto.setUserId(post.getUserId());
        dto.setBookId(post.getBookId());
        dto.setPostTitle(post.getPostTitle());
        dto.setPostContent(post.getPostContent());
        dto.setPostDate(post.getPostDate());
        dto.setPostViewCnt(post.getPostViewCnt());
        dto.setPostLastModified(post.getPostLastModified());
        dto.setLikeCount(post.getLikeCount()); //dto 수정
        dto.setLiked(post.isLiked());
        return dto;
    }



}
