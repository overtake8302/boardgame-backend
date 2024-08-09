package com.elice.boardgame.post.controller;

import com.elice.boardgame.common.dto.CommonResponse;
import com.elice.boardgame.game.dto.ClickLikeResponseDto;
import com.elice.boardgame.post.dto.CommentDto;
import com.elice.boardgame.common.enums.Enums;
import com.elice.boardgame.post.dto.PostDto;
import com.elice.boardgame.post.dto.SearchPostResponse;
import com.elice.boardgame.post.entity.Post;
import com.elice.boardgame.post.service.PostService;
import com.elice.boardgame.post.service.S3Uploader;

import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.stream.Collectors;

@Slf4j
@RequestMapping("/posts")
@RestController
public class PostController {
    private final PostService postService;
    private final S3Uploader s3Uploader;

    public PostController(PostService postService, S3Uploader s3Uploader) {
        this.postService = postService;
        this.s3Uploader = s3Uploader;
    }

    //  게시글 생성
    @PostMapping("/insert")
    public ResponseEntity<Long> createPost(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("category") String category,
            @RequestParam("gameName") String gameName,
            @RequestParam("gameId") Long gameId,
            @RequestParam(value = "file", required = false) MultipartFile[] file
    ) {
        try {
            PostDto postDto = new PostDto();
            postDto.setTitle(title);
            postDto.setContent(content);
            postDto.setCategory(category);
            postDto.setGameName(gameName);
            postDto.setGameId(gameId);

            Post createdPost = postService.createPost(postDto, file);
            return new ResponseEntity<>(createdPost.getId(), HttpStatus.CREATED);
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

    //  조회수 증가
    @PostMapping("/{category}/{post_id}/increment-view")
    public ResponseEntity<PostDto> incrementViewAndGetPost(@PathVariable String category, @PathVariable("post_id") Long id) {
        try {
            PostDto postDto = postService.incrementViewAndGetPost(category, id);
            if (postDto == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(postDto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    //  좋아요~
    @PostMapping("/{category}/{post_id}/like")
    public ResponseEntity<CommonResponse<PostDto>> clickLike(@PathVariable("post_id") Long postId) {//@RequestParam @Min(1) Long postId) {

        PostDto postDto = postService.clickLike(postId);
        CommonResponse<PostDto> response = CommonResponse.<PostDto>builder()
            .payload(postDto)
            .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
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
        log.info("PostDto {}",postDetails);
        Post updatedPost = postService.updatePostByCategory(id, category, postDetails);
        return ResponseEntity.ok(updatedPost);
    }

    //  카테고리별로 게시글 삭제
    @DeleteMapping("/{category}/{post_id}/delete")  //  경로를 어떻게할지 고민
    public ResponseEntity<Void> deletePostByCategory(@PathVariable("post_id") Long id, @PathVariable String category) {
        postService.deletePostByCategory(id, category);
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