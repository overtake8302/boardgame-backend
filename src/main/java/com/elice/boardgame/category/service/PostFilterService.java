package com.elice.boardgame.category.service;

import com.elice.boardgame.category.dto.PostListResponseDto;
import com.elice.boardgame.category.mapper.PostListMapper;
import com.elice.boardgame.post.entity.Post;
import com.elice.boardgame.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostFilterService {

    private final PostRepository postRepository;

    public Page<PostListResponseDto> findAllByType(Pageable pageable, String sortBy, String boardType) {
        Page<Post> posts = postRepository.findAllByType(pageable, sortBy, boardType);
        return PostListMapper.toDtoPage(posts);
    }

    public Page<PostListResponseDto> searchByQuery(Pageable pageable, String query, String boardType) {
        Page<Post> posts = postRepository.searchByQuery(pageable, query, boardType);
        return PostListMapper.toDtoPage(posts);
    }
}
