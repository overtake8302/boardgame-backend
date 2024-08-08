package com.elice.boardgame.post.service;

import com.elice.boardgame.post.dto.CommentDto;
import com.elice.boardgame.post.entity.Comment;
import com.elice.boardgame.post.entity.Post;
import com.elice.boardgame.post.repository.CommentRepository;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {
    private final CommentRepository commentRepository;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public Comment updateComment(Long id, Comment comment) {
        Comment updatedComment = commentRepository.findById(id).orElse(null);
        updatedComment.setContent(comment.getContent());
        return commentRepository.save(updatedComment);
    }

    @Transactional
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    public List<Comment> getCommentsByPost(Post post) {
        return commentRepository.findByPost(post);
    }

    public Comment getCommentById(Long id) {
        return commentRepository.findById(id).orElse(null);
    }

    public List<CommentDto> getReplies(Long parentId) {
        List<Comment> comments = commentRepository.findAllByParentId(parentId);
        return comments.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public CommentDto convertToDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
//        dto.setUserId(comment.getUser().getId());
        dto.setParentId(comment.getParent() != null ? comment.getParent().getId() : null);
        dto.setComInComs(comment.getComInComs().stream().map(this::convertToDto).collect(Collectors.toList()));
        dto.setReplies(comment.getComInComs().stream().map(this::convertToDto).collect(Collectors.toList()));
        if (comment.getCreatedAt() != null) {
            dto.setCreatedAt(comment.getCreatedAt().format(formatter));
        }
        return dto;
    }

    @Transactional
    public Comment createReply(Long parentId, Comment comment) {
        Comment parentComment = commentRepository.findById(parentId).orElseThrow(() -> new IllegalArgumentException("Invalid parent comment ID"));
        comment.setParent(parentComment);
        return commentRepository.save(comment);
    }

    @Transactional
    public Comment updateReply(Long replyId, Comment comment) {
        Comment existingReply = commentRepository.findById(replyId).orElseThrow(() -> new IllegalArgumentException("Invalid reply ID"));
        existingReply.setContent(comment.getContent());
        return commentRepository.save(existingReply);
    }

    @Transactional
    public void deleteReply(Long commentId) {
        Comment reply = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));
        commentRepository.delete(reply);
    }

    public Page<Comment> getCommentsByPost(Post post, Pageable pageable) {
        List<Comment> comments = commentRepository.findByPost(post);
        List<Comment> allComments = new ArrayList<>();

        for (Comment comment : comments) {
            allComments.add(comment);
            allComments.addAll(comment.getComInComs());
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allComments.size());
        List<Comment> paginatedComments = allComments.subList(start, end);

        List<Comment> filteredComments = paginatedComments.stream()
            .filter(comment -> comment.getParent() == null)
            .collect(Collectors.toList());

        return new PageImpl<>(filteredComments, pageable, allComments.size());
    }
}