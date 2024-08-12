package com.elice.boardgame.post.repository;

import com.elice.boardgame.post.entity.CommentLike;
import com.elice.boardgame.post.entity.CommentLikePK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, CommentLikePK> {
    boolean existsByCommentLikePK(CommentLikePK commentLikePK);
    long countLikesByCommentId(Long commentId);
    void deleteByCommentLikePK(CommentLikePK commentLikePK);
}
