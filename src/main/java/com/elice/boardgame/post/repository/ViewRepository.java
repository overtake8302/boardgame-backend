package com.elice.boardgame.post.repository;

import com.elice.boardgame.post.entity.View;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ViewRepository extends JpaRepository<View, Long> {
//    Optional<View> findByPostId(Long postId);
}