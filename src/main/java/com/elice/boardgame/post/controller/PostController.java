package com.elice.boardgame.post.controller;


//import com.elice.boardgame.enums.Enums.Category;
import com.elice.boardgame.post.dto.CommentDto;
import com.elice.boardgame.post.dto.PostDto;
import com.elice.boardgame.post.entity.Post;
import com.elice.boardgame.post.service.PostService;
import com.elice.boardgame.post.service.ViewService;
import com.elice.boardgame.post.service.S3Uploader;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.stream.Collectors;

@RequestMapping("/posts")
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
            @RequestParam(value = "file", required = false) MultipartFile[] file
//            @RequestParam("userID") Long userId
    ) {
        try {
            PostDto postDto = new PostDto();
            postDto.setTitle(title);
            postDto.setContent(content);
            postDto.setCategory(category);
            postDto.setGameName(gameName);
            postDto.setGameId(gameId);

//            Post createdPost = postService.createPost(postDto, file, userId);
            Post createdPost = postService.createPost(postDto, file);
            return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    //  카테고리별로 게시글 상세 조회
    @GetMapping("/{category}/{post_id}")
    public ResponseEntity<PostDto> getPostByCategoryAndId(@PathVariable String category, @PathVariable("post_id") Long id) {
        try {
            PostDto postDto = postService.getPostDtoByCategoryAndId(category, id);
            if (postDto == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(postDto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    //  수정을 위해 수정전 게시글 불러오기
    @GetMapping("/edit/{category}/{post_id}")
    public ResponseEntity<PostDto> getPostForEdit(@PathVariable String category, @PathVariable("post_id") Long id) {
        try {
            PostDto postDto = postService.getPostDtoByCategoryAndId(category, id);
            if (postDto == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(postDto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    //  카테고리별로 게시글 수정
    @PutMapping("/editok/{category}/{post_id}")
    public ResponseEntity<Post> updatePostByCategory(
            @PathVariable("post_id") Long id,
            @PathVariable String category,
            @RequestBody PostDto postDetails) {
        Post updatedPost = postService.updatePostByCategory(id, category, postDetails);
        return ResponseEntity.ok(updatedPost);
    }

    //  카테고리별로 게시글 삭제
    @DeleteMapping("/{category}/{post_id}/delete")  //  경로를 어떻게할지 고민
    public ResponseEntity<Void> deletePostByCategory(@PathVariable("post_id") Long id, @PathVariable String category) {
        postService.deletePostByCategory(id, category);
        return ResponseEntity.noContent().build();
    }
}