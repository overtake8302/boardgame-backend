package com.elice.boardgame.post.repository;

import com.elice.boardgame.common.enums.Enums;
import com.elice.boardgame.post.entity.Post;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByCategoryAndId(Enums.Category category, Long id);
}