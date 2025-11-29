package com.bookspace.domain.post.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

        import com.bookspace.domain.post.dto.PostRequestDto;
import com.bookspace.domain.post.dto.PostResponseDto;
import com.bookspace.domain.post.service.PostService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    // 1. 게시글 등록
    @PostMapping
    public ResponseEntity<String> createPost(@RequestBody PostRequestDto dto) {
        postService.createPost(dto);
        return ResponseEntity.ok("Post created successfully");
    }

    // 2. 전체 조회
    @GetMapping
    public ResponseEntity<List<PostResponseDto>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    // 3. 단건 조회
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> getPost(@PathVariable long postId) {
        return ResponseEntity.ok(postService.getPostById(postId));
    }

    // 4. 게시글 수정
    @PutMapping("/{postId}")
    public ResponseEntity<String> updatePost(
            @PathVariable long postId,
            @RequestBody PostRequestDto dto
    ) {
        postService.updatePost(postId, dto);
        return ResponseEntity.ok("Post updated successfully");
    }

    // ============================
    // 5. 게시글 삭제
    // ============================
    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable long postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok("Post deleted successfully");
    }
}
