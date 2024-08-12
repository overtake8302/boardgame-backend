package com.elice.boardgame.post.controller;

import com.elice.boardgame.common.dto.CommonResponse;
import com.elice.boardgame.game.dto.ClickLikeResponseDto;
import com.elice.boardgame.post.dto.CommentDto;
import com.elice.boardgame.post.entity.Comment;
import com.elice.boardgame.post.entity.Post;
import com.elice.boardgame.post.service.CommentService;
import com.elice.boardgame.post.service.PostService;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;
    private final PostService postService;

    public CommentController(CommentService commentService, PostService postService) {
        this.commentService = commentService;
        this.postService = postService;
    }

    //  댓글 조회
    @GetMapping("/{post_id}")
    public ResponseEntity<Page<CommentDto>> getComments(@PathVariable("post_id") Long postId,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        if (size < 1 || size > 10) {
            size = 10;
        }
        Post post = postService.getPostById(postId);
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> comments = commentService.getCommentsByPost(post, pageable);
        Page<CommentDto> commentDtos = comments.map(commentService::convertToDto);
        return ResponseEntity.ok(commentDtos);
    }

    //  댓글 좋아요
    @PostMapping("/like")
    public ResponseEntity<CommonResponse<ClickLikeResponseDto>> clickLike(@RequestParam @Min(1) Long commentId) {
        ClickLikeResponseDto clickLikeResponseDto = commentService.clickLike(commentId);
        CommonResponse<ClickLikeResponseDto> response = CommonResponse.<ClickLikeResponseDto>builder()
                .payload(clickLikeResponseDto)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //  댓글 생성
    @PostMapping("/{post_id}/insert")
    public ResponseEntity<CommentDto> createComment(@PathVariable("post_id") Long postId, @RequestBody CommentDto commentDto) {
        CommentDto createdCommentDto = commentService.createComment(postId, commentDto);

        return ResponseEntity.ok(createdCommentDto);
    }

    //  댓글 수정
    @PutMapping("/{comment_id}/edit")
    public ResponseEntity<Comment> updateComment(@PathVariable("comment_id") Long commentId, @RequestBody Comment comment) {
        Comment updatedComment = commentService.updateComment(commentId, comment); // 댓글 수정 요청 처리
        return ResponseEntity.ok(updatedComment);
    }

    //  댓글 삭제
    @DeleteMapping("/{comment_id}/delete")
    public ResponseEntity<Page<CommentDto>> deleteComment(@PathVariable("comment_id") Long commentId,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
        if (size < 1 || size > 10) {
            size = 10;
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<CommentDto> commentDtos = commentService.deleteComment(commentId, pageable);
        return ResponseEntity.ok(commentDtos);
    }

    //  대댓글 조회
    @GetMapping("/{post_id}/{comment_id}")  // 게시글 아이디와 댓글아이디만 받는거만 있어도됨
    public ResponseEntity<List<CommentDto>> getReplies(@PathVariable("post_id") Long postId, @PathVariable("comment_id") Long commentId) {
        List<CommentDto> replies = commentService.getReplies(commentId);
        return ResponseEntity.ok(replies);
    }

    //  대댓글 생성
    @PostMapping("/{comment_id}/reply/insert")
    public ResponseEntity<Comment> createReply(@PathVariable("comment_id") Long commentId, @RequestBody Comment comment) {
        Comment createdReply = commentService.createReply(commentId, comment);
        return ResponseEntity.ok(createdReply);
    }

    //  대댓글 수정
    @PutMapping("/{comment_id}/reply/edit")
    public ResponseEntity<Comment> updateReply(@PathVariable("comment_id") Long commentId, @RequestBody Comment comment) {
        Comment updatedReply = commentService.updateReply(commentId, comment);
        return ResponseEntity.ok(updatedReply);
    }

    //  대댓글 삭제
    @DeleteMapping("/{comment_id}/reply/delete")
    public ResponseEntity<Page<CommentDto>> deleteReply(@PathVariable("comment_id") Long commentId,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        if (size < 1 || size > 10) {
            size = 10;
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<CommentDto> commentDtos = commentService.deleteReply(commentId, pageable);
        return ResponseEntity.ok(commentDtos);
    }

    //  대댓글 좋아요
    @PostMapping("/reply/like")
    public ResponseEntity<CommonResponse<ClickLikeResponseDto>> replyClickLike(@RequestParam @Min(1) Long commentId) {
        ClickLikeResponseDto clickLikeResponseDto = commentService.clickLike(commentId);
        CommonResponse<ClickLikeResponseDto> response = CommonResponse.<ClickLikeResponseDto>builder()
                .payload(clickLikeResponseDto)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}