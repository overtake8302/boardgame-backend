package com.elice.boardgame.category.service;

import com.elice.boardgame.category.dto.PostListResponseDto;
import com.elice.boardgame.category.dto.PostPageDto;
import com.elice.boardgame.category.mapper.PostListMapper;
import com.elice.boardgame.post.entity.Post;
import com.elice.boardgame.post.repository.PostRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.endpoints.internal.Value.Str;

@Service
@RequiredArgsConstructor
public class PostFilterService {

    private final PostRepository postRepository;

    public PostPageDto<PostListResponseDto> find(Pageable pageable, String sortBy, String boardType) {
        PostPageDto<PostListResponseDto> postPageDto;
        if (boardType.equals("ALL")) {
            postPageDto = findAll(pageable, sortBy);
        }
        else {
            postPageDto = findAllByType(pageable, sortBy, boardType);
        }

        return postPageDto;
    }

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
        System.out.println(posts);
        Page<PostListResponseDto> dtoPage = PostListMapper.toDtoPage(posts);

        return new PostPageDto<>(
            dtoPage.getContent(),
            dtoPage.getNumber(),
            dtoPage.getSize(),
            dtoPage.getTotalElements(),
            dtoPage.getTotalPages()
        );
    }

    public PostPageDto<PostListResponseDto> search(Pageable pageable, String query, String boardType) {
        PostPageDto<PostListResponseDto> postPageDto;
        if (boardType.equals("ALL")) {
            postPageDto = searchByQuery(pageable, query);
        }
        else {
            postPageDto = searchByQuery(pageable, query, boardType);
        }
        return postPageDto;
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
