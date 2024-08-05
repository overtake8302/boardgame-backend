package com.elice.boardgame.post.controller;

import com.elice.boardgame.post.entity.Comment;
import com.elice.boardgame.post.entity.Post;
import com.elice.boardgame.post.service.CommentService;
import com.elice.boardgame.post.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts/{category}/{post_id}/comments")  //  경로는 일단 나중에 생각해보자
public class CommentController {
    private final CommentService commentService;
    private final PostService postService;

    public CommentController(CommentService commentService, PostService postService) {
        this.commentService = commentService;
        this.postService = postService;
    }

    //  댓글 조회
    @GetMapping
    public ResponseEntity<List<Comment>> getComments(@PathVariable String category, @PathVariable("post_id") Long postId) {
        Post post = postService.getPostByCategoryAndId(category, postId);
        List<Comment> comments = commentService.getCommentsByPost(post);
        return ResponseEntity.ok(comments);
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
    @PutMapping("/edit/{commentId}")
    public ResponseEntity<Comment> updateComment(@PathVariable Long commentId, @RequestBody Comment comment) {
        Comment updatedComment = commentService.updateComment(commentId, comment); // 댓글 수정 요청 처리
        return ResponseEntity.ok(updatedComment);
    }

    //  댓글 삭제
    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId); // 댓글 삭제 요청 처리
        return ResponseEntity.noContent().build();
    }

    //  대댓글 조회
    @GetMapping("/{comment_id}/replies")
    public ResponseEntity<List<Comment>> getReplies(@PathVariable("post_id") Long postId, @PathVariable("comment_id") Long commnetId) {
        List<Comment> replies = commentService.getReplies(commnetId); // 자식 댓글 조회 요청 처리
        return ResponseEntity.ok(replies);
    }
}