package com.elice.boardgame.post.controller;

import com.elice.boardgame.post.entity.Post;
import com.elice.boardgame.post.entity.Post.Category;
import com.elice.boardgame.post.service.PostService;
import com.elice.boardgame.post.service.ViewService;
import com.elice.boardgame.post.service.S3Uploader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/posts")  //  경로가....뭐로해야될까....
@RestController
public class PostController {
    @Autowired
    private ViewService viewService;

    private final PostService postService;
    private final S3Uploader s3Uploader;

    @Autowired
    public PostController(PostService postService, S3Uploader s3Uploader) {
        this.postService = postService;
        this.s3Uploader = s3Uploader;
    }

    //  게시글 생성
    @PostMapping("/create")
    public ResponseEntity<Post> createPost(@RequestBody Post post, @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        String imageUrl = null;  //  이미지 URL을 저장할 변수 초기화
        if (file != null && !file.isEmpty()) {  //  파일이 존재하고 비어있지 않은 경우
            String fileName = file.getOriginalFilename();  //  파일의 원래 이름을 가져옴
            Path tempFile = Files.createTempFile("temp", fileName);  //  임시 파일 생성
            file.transferTo(tempFile.toFile());  //  업로드된 파일을 임시 파일로 복사
            imageUrl = s3Uploader.upload(tempFile.toString(), fileName);  //  S3에 파일 업로드 후 URL 반환
            Files.delete(tempFile);  //  임시 파일 삭제
        }

        Post createdPost = postService.createPost(post, imageUrl);  //  PostService를 사용하여 게시글 생성
        return ResponseEntity.ok(createdPost);  //  생성된 게시글을 포함한 응답 반환
    }

    //  카테고리별로 게시글 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostByCategoryAndId(@PathVariable Category category, @PathVariable Long id) {
        Post post = postService.getPostByCategoryAndId(category, id);
        return ResponseEntity.ok(post);
    }

    //  카테고리별로 게시글 수정
    @PutMapping("/{id}/update")
    public ResponseEntity<Post> updatePostByCategory(@PathVariable Long id, @PathVariable Category category, @RequestBody Post postDetails, @RequestParam Long userId) {
        Post updatedPost = postService.updatePostByCategory(id, category, postDetails, userId);
        return ResponseEntity.ok(updatedPost);
    }

    //  카테고리별로 게시글 삭제
    @DeleteMapping("/{id}/delete")  //  경로를 어떻게할지 고민
    public ResponseEntity<Void> deletePostByCategory(@PathVariable Long id, @PathVariable Category category, @RequestParam Long userId) {
        postService.deletePostByCategory(id, category, userId);
        return ResponseEntity.noContent().build();
    }
}