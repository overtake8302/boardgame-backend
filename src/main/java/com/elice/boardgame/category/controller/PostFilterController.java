package com.elice.boardgame.category.controller;

import com.elice.boardgame.category.dto.BoardRequestDto;
import com.elice.boardgame.category.dto.PostListResponseDto;
import com.elice.boardgame.category.dto.PostPageDto;
import com.elice.boardgame.category.service.PostFilterService;
import com.elice.boardgame.common.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "게시판 필터, 검색", description = "게시판에서 선택한 정렬기준과 검색어에 따라 적합한 게시물을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공적으로 필터링된 게시물 목록 조회됨"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping
    public ResponseEntity<CommonResponse<PostPageDto<PostListResponseDto>>> findAllByBoardType(BoardRequestDto boardRequestDto) {
        PostPageDto<PostListResponseDto> postPageDto = postFilterService.find(boardRequestDto);

        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(3, TimeUnit.SECONDS))
            .body(CommonResponse.<PostPageDto<PostListResponseDto>>builder()
                .payload(postPageDto)
                .message("")
                .status(200)
                .build());
    }
}
