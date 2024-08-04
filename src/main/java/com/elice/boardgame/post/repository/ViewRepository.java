package com.elice.boardgame.post.repository;

import com.elice.boardgame.post.entity.View;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViewRepository extends JpaRepository<View, Long> {
    View findByPostId(Long postId);
}