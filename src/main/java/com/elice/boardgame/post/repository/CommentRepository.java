package com.elice.boardgame.post.repository;

import com.elice.boardgame.post.entity.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByParentId(Long parentId);

    List<Comment> findAllByUser_Id(Long id);
}