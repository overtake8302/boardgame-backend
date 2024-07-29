package com.elice.boardgame.post.repository;

import com.elice.boardgame.post.entity.Post;
import com.elice.boardgame.post.entity.Post.Category;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByCategoryAndId(Category category, Long id);
}