package com.elice.boardgame.category.controller;

import com.elice.boardgame.category.dto.PostListResponseDto;
import com.elice.boardgame.category.dto.PostPageDto;
import com.elice.boardgame.category.service.PostFilterService;
import com.elice.boardgame.common.dto.CommonResponse;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<CommonResponse<PostPageDto<PostListResponseDto>>> findAllByBoardType(Pageable pageable,
        @RequestParam(name = "sortBy", required = false, defaultValue = "createdAt") String sortBy,
        @RequestParam(name = "boardType") String boardType) {

        PostPageDto<PostListResponseDto> postPageDto = postFilterService.find(pageable, sortBy, boardType);


        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(10, TimeUnit.SECONDS))
            .body(CommonResponse.<PostPageDto<PostListResponseDto>>builder()
                .payload(postPageDto)
                .message("")
                .status(200)
                .build());
    }

    // 검색 엔드포인트
    @GetMapping("/search")
    public CommonResponse<PostPageDto<PostListResponseDto>> searchPosts(
        Pageable pageable,
        @RequestParam(name = "query") String query,
        @RequestParam(name = "boardType") String boardType) {

        PostPageDto<PostListResponseDto> postPageDto = postFilterService.search(pageable, query, boardType);

        return CommonResponse.<PostPageDto<PostListResponseDto>>builder()
            .payload(postPageDto)
            .message("")
            .status(200)
            .build();
    }
}
