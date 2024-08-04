package com.elice.boardgame.post.service;

import com.elice.boardgame.post.entity.Comment;
import com.elice.boardgame.post.repository.CommentRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    public List<Comment> getReplies(Long parentId) {
        return commentRepository.findAllByParentId(parentId);
    }
}