package com.elice.boardgame.post.controller;

import com.elice.boardgame.common.dto.CommonResponse;
import com.elice.boardgame.common.dto.SearchRequest;
import com.elice.boardgame.common.dto.SearchResponse;
import com.elice.boardgame.game.dto.ClickLikeResponseDto;
import com.elice.boardgame.post.dto.PostDto;
import com.elice.boardgame.post.dto.SearchPostResponse;
import com.elice.boardgame.post.entity.Post;
import com.elice.boardgame.post.service.PostService;
import com.elice.boardgame.post.service.S3Uploader;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequestMapping("/post")
@RestController
@Tag(name = "Post", description = "게시글 API")
public class PostController {
    private final PostService postService;
    private final S3Uploader s3Uploader;

    public PostController(PostService postService, S3Uploader s3Uploader) {
        this.postService = postService;
        this.s3Uploader = s3Uploader;
    }

    @Operation(summary = "게시글 생성", description = "게시글의 생성을 처리합니다.")
    @PostMapping("/insert")
    public ResponseEntity<Long> createPost(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("category") String category,
            @RequestParam("gameName") String gameName,
            @RequestParam("game_id") Long gameId,
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

    @Operation(summary = "게시글 조회", description = "게시글을 조회할 수 있습니다.")
    @GetMapping("/{post_id}")
    public ResponseEntity<PostDto> getPostByCategoryAndId(@PathVariable("post_id") Long id) {
        try {
            PostDto postDto = postService.getPostDtoById(id);
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
//    @PostMapping("/{post_id}/increment-view")
//    public ResponseEntity<PostDto> incrementViewAndGetPost(@PathVariable("post_id") Long id) {
//        try {
//            PostDto postDto = postService.incrementViewAndGetPost(id);
//            if (postDto == null) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//            }
//            return ResponseEntity.ok(postDto);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }

    @Operation(summary = "게시글 조회수 증가", description = "게시글의 조회수를 증가시키는 기능을 합니다.")
    @PostMapping("/{post_id}/increment-view")
    public void incrementViewAndGetPost(@RequestHeader("visitor-id") @NotBlank String visitorId, @RequestHeader("post-id") @Min(1) Long postId) {
        postService.incrementViewAndGetPost(visitorId, postId);
    }

    @Operation(summary = "좋아요 기능", description = "게시글, 댓글, 대댓글의 좋아요 기능을 합니다.")
    @PostMapping("/like")
    public ResponseEntity<CommonResponse<ClickLikeResponseDto>> clickLike(@RequestParam @Min(1) Long postId) {
        ClickLikeResponseDto clickLikeResponseDto = postService.clickLike(postId);
        CommonResponse<ClickLikeResponseDto> response = CommonResponse.<ClickLikeResponseDto>builder()
                .payload(clickLikeResponseDto)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Operation(summary = "게시글 수정전의 내용 조회", description = "게시글의 수정시 수정전의 게시글 내용을 보여주는 기능을 합니다.")
    @GetMapping("/{post_id}/edit")
    public ResponseEntity<PostDto> getPostForEdit(@PathVariable("post_id") Long id) {
        try {
            PostDto postDto = postService.getPostDtoById(id);
            if (postDto == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(postDto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "게시글 수정", description = "게시글의 수정 기능을 합니다.")
    @PutMapping("/{post_id}/editok")
    public ResponseEntity<Post> updatePostByCategory(
            @PathVariable("post_id") Long id,
            @RequestBody PostDto postDetails) {
        Post updatedPost = postService.updatePost(id, postDetails);
        return ResponseEntity.ok(updatedPost);
    }

    @Operation(summary = "게시글 삭제", description = "게시글의 삭제 기능을 합니다.")
    @DeleteMapping("/{post_id}/delete")
    public ResponseEntity<Void> deletePostByCategory(@PathVariable("post_id") Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "게시글 검색", description = "키워드로 게시글을 검색하는 기능을 합니다.")
    @GetMapping("/search")
    public ResponseEntity<CommonResponse<Page<SearchPostResponse>>> searchPosts(@ModelAttribute SearchRequest searchRequest) {
        Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize());
        String keyword = searchRequest.getKeyword();
        CommonResponse<Page<SearchPostResponse>> response = postService.searchPostsByKeyword(keyword, pageable);
        return ResponseEntity.ok(response);
    }
}