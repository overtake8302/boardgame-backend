package com.elice.boardgame.post.repository;

import com.elice.boardgame.post.entity.PostLike;
import com.elice.boardgame.post.entity.PostLikePK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, PostLikePK> {
    boolean existsByPostLikePK(PostLikePK postLikePK);
    Long countLikesByPostId(Long postId);
    void deleteByPostLikePK(PostLikePK postLikePK);
}
