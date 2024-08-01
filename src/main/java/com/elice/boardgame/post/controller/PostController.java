package com.elice.boardgame.post.controller;

import com.elice.boardgame.enums.Enums;
//import com.elice.boardgame.enums.Enums.Category;
import com.elice.boardgame.post.dto.PostDto;
import com.elice.boardgame.post.entity.Post;
import com.elice.boardgame.post.service.PostService;
import com.elice.boardgame.post.service.ViewService;
import com.elice.boardgame.post.service.S3Uploader;

import java.util.List;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<Post> createPost(
        @RequestParam("title") String title,
        @RequestParam("content") String content,
        @RequestParam("category") String category,
        @RequestParam("gameName") String gameName,
        @RequestParam("gameId") Long gameId,
        @RequestParam(value = "file", required = false) MultipartFile file
//        @RequestParam("userID") Long userId
    ) throws Exception {

        PostDto postDto = new PostDto();
        postDto.setTitle(title);
        postDto.setContent(content);
        postDto.setCategory(category);
        postDto.setGameName(gameName);
        postDto.setGameId(gameId);

        if (file != null) {
            try {
                String imageUrl = s3Uploader.uploadFile(file); // 파일 업로드 및 URL 반환
                postDto.setImageUrl(imageUrl); // PostDto 객체에 이미지 URL 설정
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }

//        Post createdPost = postService.createPost(postDto, file, userId);
        Post createdPost = postService.createPost(postDto, file);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    //  카테고리별로 게시글 상세 조회
    @GetMapping("/{post_id}")
    public ResponseEntity<Post> getPostByCategoryAndId(@PathVariable String category, @PathVariable("post_id") Long id) {
        Post post = postService.getPostByCategoryAndId(category, id);
        return ResponseEntity.ok(post);
    }

    //  카테고리별로 게시글 수정
    @PutMapping("/{id}/test2")
    public ResponseEntity<Post> updatePostByCategory(@PathVariable Long id, @PathVariable String category, @RequestBody PostDto postDetails, @RequestParam Long userId) {
        Post updatedPost = postService.updatePostByCategory(id, category, postDetails, userId);
        return ResponseEntity.ok(updatedPost);
    }

    //  카테고리별로 게시글 삭제
    @DeleteMapping("/{id}/test3")  //  경로를 어떻게할지 고민
    public ResponseEntity<Void> deletePostByCategory(@PathVariable Long id, @PathVariable String category, @RequestParam Long userId) {
        postService.deletePostByCategory(id, category, userId);
        return ResponseEntity.noContent().build();
    }
}