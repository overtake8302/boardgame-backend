package com.elice.boardgame.post.repository;
import com.elice.boardgame.game.dto.PostsByGame;
import com.elice.boardgame.post.dto.PostDto;
import com.elice.boardgame.post.dto.SearchPostResponse;
import com.elice.boardgame.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomPostRepository {
    Page<Post> search(Pageable pageable, String query, String boardType, String sortBy);
    Page<SearchPostResponse> searchPostsByKeyword(String keyword, Pageable pageable);
    List<PostsByGame> findTop10ByBoardGameGameIdAndCategoryAndDeletedAtIsNullOrderByIdDesc(Long gameId, String category);
    Post findPostById(Long id);
}
