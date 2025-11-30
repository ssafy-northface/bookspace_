package com.bookspace.domain.post.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookspace.domain.post.dao.PostDao;
import com.bookspace.domain.post.dto.PostRequestDto;
import com.bookspace.domain.post.dto.PostResponseDto;
import com.bookspace.domain.post.vo.PostVo;

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

    @Override
    public List<PostResponseDto> getAllPosts() {
        return postDao.selectAllPosts()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public PostResponseDto getPostById(long postId) {
        postDao.increaseViewCount(postId);
        return convertToResponseDto(postDao.selectPostById(postId));
    }

    @Override
    public void updatePost(long postId, PostRequestDto dto) {
        PostVo vo = new PostVo();
        vo.setPostId(postId);
        vo.setPostTitle(dto.getPostTitle());
        vo.setPostContent(dto.getPostContent());

        postDao.updatePost(vo);
    }

    @Override
    public void deletePost(long postId) {
        postDao.deletePost(postId);
    }

    @Override
    public void increaseViewCount(long postId) {
        postDao.increaseViewCount(postId);
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

        return dto;
    }
}
