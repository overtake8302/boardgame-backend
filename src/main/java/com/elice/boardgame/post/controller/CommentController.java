package com.elice.boardgame.post.controller;

import com.elice.boardgame.post.dto.CommentDto;
import com.elice.boardgame.post.entity.Comment;
import com.elice.boardgame.post.entity.Post;
import com.elice.boardgame.post.service.CommentService;
import com.elice.boardgame.post.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts/{category}/{post_id}/comments")  //  경로는 일단 나중에 생각해보자  comment만 있으면 되고 나머지도 insert등 빼도됨
public class CommentController {
    private final CommentService commentService;
    private final PostService postService;

    public CommentController(CommentService commentService, PostService postService) {
        this.commentService = commentService;
        this.postService = postService;
    }

    //  댓글 조회
    @GetMapping
    public ResponseEntity<Page<CommentDto>> getComments(@PathVariable String category, @PathVariable("post_id") Long postId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        Post post = postService.getPostByCategoryAndId(category, postId);
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> comments = commentService.getCommentsByPost(post, pageable);
        Page<CommentDto> commentDtos = comments.map(commentService::convertToDto);
        return ResponseEntity.ok(commentDtos);
    }

    //  댓글 생성
    @PostMapping("/insert")
    public ResponseEntity<Comment> createComment(@PathVariable String category, @PathVariable("post_id") Long postId, @RequestBody Comment comment) {
        Post post = postService.getPostByCategoryAndId(category, postId);
        comment.setPost(post);
        Comment createdComment = commentService.createComment(comment);
        return ResponseEntity.ok(createdComment);
    }

    //  댓글 수정
    @PutMapping("/edit/{comment_id}")
    public ResponseEntity<Comment> updateComment(@PathVariable("comment_id") Long commentId, @RequestBody Comment comment) {
        Comment updatedComment = commentService.updateComment(commentId, comment); // 댓글 수정 요청 처리
        return ResponseEntity.ok(updatedComment);
    }

    //  댓글 삭제
    @DeleteMapping("/delete/{comment_id}")
    public ResponseEntity<Void> deleteComment(@PathVariable("comment_id") Long commentId) {
        commentService.deleteComment(commentId); // 댓글 삭제 요청 처리
        return ResponseEntity.noContent().build();
    }

    //  대댓글 조회
    @GetMapping("/{comment_id}/replies")  // 게시글 아이디와 댓글아이디만 받는거만 있어도됨
    public ResponseEntity<List<CommentDto>> getReplies(@PathVariable("post_id") Long postId, @PathVariable("comment_id") Long commentId) {
        List<CommentDto> replies = commentService.getReplies(commentId);
        return ResponseEntity.ok(replies);
    }

    //  대댓글 생성
    @PostMapping("/reply/{comment_id}/insert")
    public ResponseEntity<Comment> createReply(@PathVariable("post_id") Long postId, @PathVariable("comment_id") Long commentId, @RequestBody Comment comment) {
        Comment createdReply = commentService.createReply(commentId, comment);
        return ResponseEntity.ok(createdReply);
    }

    //  대댓글 수정
    @PutMapping("/reply/{comment_id}/edit")
    public ResponseEntity<Comment> updateReply(@PathVariable("comment_id") Long commentId, @RequestBody Comment comment) {
        Comment updatedReply = commentService.updateReply(commentId, comment);
        return ResponseEntity.ok(updatedReply);
    }

    //  대댓글 삭제
    @DeleteMapping("/reply/{comment_id}/delete")
    public ResponseEntity<Void> deleteReply(@PathVariable("comment_id") Long commentId) {
        commentService.deleteReply(commentId);
        return ResponseEntity.noContent().build();
    }
}