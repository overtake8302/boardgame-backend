package com.elice.boardgame.post.repository;

import com.elice.boardgame.post.entity.Comment;
import java.util.List;

import com.elice.boardgame.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>, CustomCommentRepository {
    List<Comment> findAllByParentId(Long parentId);

//    Page<Comment> findAllByUser_IdAndDeletedAtIsNull(Long id, Pageable pageable);

    List<Comment> findByPost(Post post);

    Comment findByIdAndDeletedAtIsNull(Long commentId);
}