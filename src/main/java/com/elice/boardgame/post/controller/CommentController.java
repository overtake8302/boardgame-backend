package com.elice.boardgame.post.controller;

import com.elice.boardgame.post.entity.Comment;
import com.elice.boardgame.post.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts/{postId}")  //  경로는 일단 나중에 생각해보자
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/insert")
    public ResponseEntity<Comment> createComment(@RequestBody Comment comment) {
        Comment createdComment = commentService.createComment(comment); // 댓글 생성 요청 처리
        return ResponseEntity.ok(createdComment);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id); // 댓글 삭제 요청 처리
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/replies/{parentId}")
    public ResponseEntity<List<Comment>> getReplies(@PathVariable Long parentId) {
        List<Comment> replies = commentService.getReplies(parentId); // 자식 댓글 조회 요청 처리
        return ResponseEntity.ok(replies);
    }
}