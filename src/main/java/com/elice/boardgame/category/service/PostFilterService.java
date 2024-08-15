package com.elice.boardgame.category.service;

import com.elice.boardgame.category.dto.BoardRequestDto;
import com.elice.boardgame.category.dto.PostListResponseDto;
import com.elice.boardgame.category.dto.PostPageDto;
import com.elice.boardgame.category.mapper.PostListMapper;
import com.elice.boardgame.post.dto.PostDto;
import com.elice.boardgame.post.entity.Post;
import com.elice.boardgame.post.repository.PostRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.endpoints.internal.Value.Str;

@Service
@RequiredArgsConstructor
public class PostFilterService {

    private final PostRepository postRepository;

    public PostPageDto<PostListResponseDto> find(BoardRequestDto boardRequestDto) {
        Pageable pageable = PageRequest.of(boardRequestDto.getPage(), boardRequestDto.getSize());
        String sortBy = boardRequestDto.getSortBy();
        String boardType = boardRequestDto.getBoardType();
        String query = boardRequestDto.getQuery();

        PostPageDto<PostListResponseDto> postPageDto;

        Page<Post> posts = postRepository.search(pageable, query, boardType, sortBy);
        Page<PostListResponseDto> dtoPage = PostListMapper.toDtoPage(posts);

        return PostPageDto.<PostListResponseDto>builder()
            .content(dtoPage.getContent())
            .pageNumber(dtoPage.getNumber())
            .pageSize(dtoPage.getSize())
            .totalElements(dtoPage.getTotalElements())
            .totalPages(dtoPage.getTotalPages())
            .build();
    }
}
