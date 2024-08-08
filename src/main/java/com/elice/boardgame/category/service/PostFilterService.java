package com.elice.boardgame.category.service;

import com.elice.boardgame.category.dto.PostListResponseDto;
import com.elice.boardgame.category.dto.PostPageDto;
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

    public PostPageDto<PostListResponseDto> findAllByType(Pageable pageable, String sortBy, String boardType) {
        Page<Post> posts = postRepository.findAllByType(pageable, sortBy, boardType);
        Page<PostListResponseDto> dtoPage = PostListMapper.toDtoPage(posts);

        return new PostPageDto<>(
            dtoPage.getContent(),
            dtoPage.getNumber(),
            dtoPage.getSize(),
            dtoPage.getTotalElements(),
            dtoPage.getTotalPages()
        );
    }

    public PostPageDto<PostListResponseDto> findAll(Pageable pageable, String sortBy) {
        Page<Post> posts = postRepository.findAll(pageable, sortBy);
        Page<PostListResponseDto> dtoPage = PostListMapper.toDtoPage(posts);

        return new PostPageDto<>(
            dtoPage.getContent(),
            dtoPage.getNumber(),
            dtoPage.getSize(),
            dtoPage.getTotalElements(),
            dtoPage.getTotalPages()
        );
    }

    public PostPageDto<PostListResponseDto> searchByQuery(Pageable pageable, String query, String boardType) {
        Page<Post> posts = postRepository.searchByQuery(pageable, query, boardType);
        Page<PostListResponseDto> dtoPage = PostListMapper.toDtoPage(posts);

        return new PostPageDto<>(
            dtoPage.getContent(),
            dtoPage.getNumber(),
            dtoPage.getSize(),
            dtoPage.getTotalElements(),
            dtoPage.getTotalPages()
        );
    }

    public PostPageDto<PostListResponseDto> searchByQuery(Pageable pageable, String query) {
        Page<Post> posts = postRepository.searchByQuery(pageable, query);
        Page<PostListResponseDto> dtoPage = PostListMapper.toDtoPage(posts);

        return new PostPageDto<>(
            dtoPage.getContent(),
            dtoPage.getNumber(),
            dtoPage.getSize(),
            dtoPage.getTotalElements(),
            dtoPage.getTotalPages()
        );
    }
}
