package com.elice.boardgame.category.controller;

import com.elice.boardgame.category.dto.PostListResponseDto;
import com.elice.boardgame.category.dto.PostPageDto;
import com.elice.boardgame.category.service.PostFilterService;
import com.elice.boardgame.common.dto.CommonResponse;
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
    public CommonResponse<PostPageDto<PostListResponseDto>> findAllByBoardType(Pageable pageable,
        @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
        @RequestParam String boardType) {

        PostPageDto<PostListResponseDto> postPageDto = postFilterService.findAllByType(pageable, sortBy, boardType);

        return CommonResponse.<PostPageDto<PostListResponseDto>>builder()
            .payload(postPageDto)
            .message("")
            .status(200)
            .build();
    }

    // 검색 엔드포인트
    @GetMapping("/search")
    public CommonResponse<PostPageDto<PostListResponseDto>> searchPosts(
        Pageable pageable,
        @RequestParam String query,
        @RequestParam String boardType) {

        PostPageDto<PostListResponseDto> postPageDto = postFilterService.searchByQuery(pageable, query, boardType);

        return CommonResponse.<PostPageDto<PostListResponseDto>>builder()
            .payload(postPageDto)
            .message("")
            .status(200)
            .build();
    }
}
