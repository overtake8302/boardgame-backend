package com.elice.boardgame.category.controller;

import com.elice.boardgame.category.dto.PostListResponseDto;
import com.elice.boardgame.category.dto.PostPageDto;
import com.elice.boardgame.category.service.PostFilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class PostFilterController {

    private final PostFilterService postFilterService;

    @GetMapping
    public PostPageDto<PostListResponseDto> findAllByBoardType(Pageable pageable,
        @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
        @RequestParam String boardType) {
        Page<PostListResponseDto> page = postFilterService.findAllByType(pageable, sortBy, boardType);
        System.out.println(page.getContent());
        return new PostPageDto<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages()
        );
    }

    // 검색 엔드포인트
    @GetMapping("/search")
    public PostPageDto<PostListResponseDto> searchPosts(
        Pageable pageable,
        @RequestParam String query,
        @RequestParam String boardType) {

        Page<PostListResponseDto> page = postFilterService.searchByQuery(pageable, query, boardType);
        return new PostPageDto<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages()
        );
    }
}
