package com.elice.boardgame.post.repository;

import com.elice.boardgame.post.entity.Comment;
import java.util.List;

import com.elice.boardgame.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByParentId(Long parentId);

    List<Comment> findByPost(Post post);
}