package com.elice.boardgame.post.repository;

import com.elice.boardgame.common.enums.Enums;
import com.elice.boardgame.game.entity.BoardGame;
import com.elice.boardgame.post.entity.Post;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>, CustomPostRepository {
    Optional<Post> findByCategoryAndId(String category, Long id);

    List<Post> findTop10ByBoardGameGameIdAndCategoryOrderByIdDesc(Long gameId, Enums.Category category);

    Post findByIdAndDeletedAtIsNull(Long postId);
}