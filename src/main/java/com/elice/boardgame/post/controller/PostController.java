package com.elice.boardgame.post.controller;

import com.elice.boardgame.common.dto.CommonResponse;
import com.elice.boardgame.common.dto.SearchRequest;
import com.elice.boardgame.common.enums.Enums;
import com.elice.boardgame.post.dto.PostDto;
import com.elice.boardgame.post.dto.SearchPostResponse;
import com.elice.boardgame.post.entity.Post;
import com.elice.boardgame.post.service.PostService;
import com.elice.boardgame.post.service.ViewService;
import com.elice.boardgame.post.service.S3Uploader;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/posts")  //  경로가....뭐로해야될까....
@RestController
public class PostController {
    private final ViewService viewService;
    private final PostService postService;
    private final S3Uploader s3Uploader;

    public PostController(PostService postService, S3Uploader s3Uploader, ViewService viewService) {
        this.postService = postService;
        this.s3Uploader = s3Uploader;
        this.viewService = viewService;
    }

    //  게시글 생성
    @PostMapping("/insert")
    public ResponseEntity<Post> createPost(@RequestBody Post post, @RequestParam("file") MultipartFile file) throws Exception {
        Post createdPost = postService.createPost(post, file);
        return ResponseEntity.ok(createdPost);
    }

    //  카테고리별로 게시글 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostByCategoryAndId(@PathVariable Enums.Category category, @PathVariable Long id) {
        Post post = postService.getPostByCategoryAndId(category, id);
        return ResponseEntity.ok(post);
    }

    //  카테고리별로 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePostByCategory(@PathVariable Long id, @PathVariable Enums.Category category, @RequestBody PostDto postDetails, @RequestParam Long userId) {
        Post updatedPost = postService.updatePostByCategory(id, category, postDetails, userId);
        return ResponseEntity.ok(updatedPost);
    }

    //  카테고리별로 게시글 삭제
    @DeleteMapping("/{id}")  //  경로를 어떻게할지 고민
    public ResponseEntity<Void> deletePostByCategory(@PathVariable Long id, @PathVariable Enums.Category category, @RequestParam Long userId) {
        postService.deletePostByCategory(id, category, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<CommonResponse<Page<SearchPostResponse>>> searchPosts(@ModelAttribute SearchRequest searchRequest) {
        Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize());
        String keyword = searchRequest.getKeyword();
        CommonResponse<Page<SearchPostResponse>> response = postService.searchPostsByKeyword(keyword, pageable);
        return ResponseEntity.ok(response);
    }
}