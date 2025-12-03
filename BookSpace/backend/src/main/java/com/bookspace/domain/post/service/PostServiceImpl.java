package com.bookspace.domain.post.service;

import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public void createPost(PostRequestDto dto) {
        PostVo vo = new PostVo();
        vo.setUserId(dto.getUserId());
        vo.setBookId(dto.getBookId());
        vo.setPostTitle(dto.getPostTitle());
        vo.setPostContent(dto.getPostContent());

        postDao.insertPost(vo);
    }


    /*
    모든 게시물 조회는 게시물 좋아요 개수, 좋아요 여부 2개가 모두 포함된 응답이 옴
    - 로그인한 유저라면, 자신이 게시물을 좋아요 눌렀는지 여부 (full heart / empty heart)
    - 로그인한 유저라면, liked값이 모두 false로 옴 -> 프론트 처리 (empty heart를 유저가 누르려고 시도했을 때 로그인 하라고 하기)

     */
    @Override
    public List<PostResponseDto> getAllPosts() {

        // TODO 로그인 로직 구현 후 수정
        Long userId = 1L; // 로그인한 유저일 경우 (1번으로 테스트)
        // Long userId = null; // 로그인 하지 않았을 때

        List<PostVo> voList = postDao.selectAllPosts(userId);

        return voList.stream()
                .map(this::convertToResponseDto)
                .toList();
    }

    @Override
    public PostResponseDto getPostById(long postId) {

        // TODO 로그인 로직 구현 후 수정
        //Long userId = 1L;
        Long userId = null;

        PostVo postVo = postDao.selectPostById(postId,userId);

        if(postVo == null){
            throw new IllegalArgumentException("Post not found with id: " + postId);
        }

        postDao.increaseViewCount(postId);
        return convertToResponseDto(postVo);
    }

    @Override
    public void updatePost(long postId, PostRequestDto dto) {
        PostVo vo = new PostVo();
        vo.setPostId(postId);
        vo.setPostTitle(dto.getPostTitle());
        vo.setPostContent(dto.getPostContent());
        int updatedRows = postDao.updatePost(vo);
        if (updatedRows == 0) {
            // 존재하지 않는 postId일 때
            throw new IllegalArgumentException("Post not found with id: " + postId);
        }    }


    @Override
    public void deletePost(long postId) {
        int deletedRows = postDao.deletePost(postId);
        if (deletedRows == 0) {
            throw new IllegalArgumentException("Post not found with id: " + postId);
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
        List<PostVo> postVoList = postDao.selectPostsByUserId(userId);
        return postVoList.stream()
                .map(this::convertToResponseDto)
                .toList();
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
