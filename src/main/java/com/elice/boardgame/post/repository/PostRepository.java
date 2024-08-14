package com.elice.boardgame.post.repository;

import com.elice.boardgame.post.entity.Post;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>, CustomPostRepository {
    List<Post> findTop10ByBoardGameGameIdAndCategoryAndDeletedAtIsNullOrderByIdDesc(Long gameId, String category);

    Page<Post> findAllByUser_IdAndDeletedAtIsNull(Long id, Pageable pageable);

    Post findByIdAndDeletedAtIsNull(Long postId);
}