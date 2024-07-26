package com.elice.boardgame.post.repository;

import com.elice.boardgame.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

}