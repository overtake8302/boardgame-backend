package com.elice.boardgame.post.repository;
import com.elice.boardgame.post.dto.SearchPostResponse;
import com.elice.boardgame.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomPostRepository {
    Page<Post> findAllByType(Pageable pageable, String sortBy, String boardType);
    Page<Post> searchByQuery(Pageable pageable, String query, String boardType);
    Page<SearchPostResponse> searchPostsByKeyword(String keyword, Pageable pageable);
}
