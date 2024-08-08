package com.elice.boardgame.post.repository;
import com.elice.boardgame.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomPostRepository {
    Page<Post> findAll(Pageable pageable, String sortBy);
    Page<Post> findAllByType(Pageable pageable, String sortBy, String boardType);
    Page<Post> searchByQuery(Pageable pageable, String query);
    Page<Post> searchByQuery(Pageable pageable, String query, String boardType);
}
