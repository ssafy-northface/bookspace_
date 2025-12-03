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

    @GetMapping("/likes")
    public ResponseEntity<List<PostResponseDto>> getLikedPosts() {
        // TODO 로그인 로직 구현 후 수정 (로그인한 유저만)
        Long userId = 1L; // userId 반드시 필요함

        List<PostResponseDto> likedPosts = postService.getLikedPostsByUserId(userId);

        return ResponseEntity.ok(likedPosts);
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

    // 5. 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable long postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok("Post deleted successfully");
    }

    // 6. bookId로 조회
    @GetMapping(params = "bookId")
    // /posts?bookId=5 => 이렇게 쿼리파라미터로 들어감
    public ResponseEntity<List<PostResponseDto>> getPostsByBookId(long bookId) {
        return ResponseEntity.ok(postService.getPostsByBookId(bookId));
    }

    // 7. userId로 조회
    @GetMapping(params = "userId")
    public ResponseEntity<List<PostResponseDto>> getPostsByUserId(long userId) {
        return ResponseEntity.ok(postService.getPostsByUserId(userId));
    }

    // 8. 키워드 검색 및 조회
    @GetMapping("/search")
    // /posts/search?keyword=강아
    public ResponseEntity<List<PostResponseDto>> getPostsByKeyword(@RequestParam String keyword) {
        return ResponseEntity.ok(postService.getPostsByKeyword(keyword));
    }


}
