package com.elice.boardgame.game.controller;

import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.auth.service.AuthService;
import com.elice.boardgame.common.annotation.CurrentUser;
import com.elice.boardgame.common.dto.CommonResponse;
import com.elice.boardgame.common.dto.SearchRequest;
import com.elice.boardgame.common.dto.SearchResponse;
import com.elice.boardgame.common.enums.Enums;
import com.elice.boardgame.common.exceptions.GameErrorMessages;
import com.elice.boardgame.common.exceptions.GameRootException;
import com.elice.boardgame.game.dto.*;
import com.elice.boardgame.game.mapper.BoardGameMapper;
import com.elice.boardgame.game.service.BoardGameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/game")
@Validated
@RequiredArgsConstructor
public class    BoardGameController {

    private final BoardGameService boardGameService;
    private final BoardGameMapper mapper;
    private final AuthService authService;

    @Operation(summary = "게임 생성", description = "게임 정보 등록을 처리 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게임 등록 성공"),
            @ApiResponse(responseCode = "400", description = "필수 입력값 누락등 검증 통과 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping
    public ResponseEntity<CommonResponse<GameResponseDto>> postGame(
            @RequestPart("gamePostDto") @Validated GamePostDto gamePostDto,
            BindingResult bindingResult,
            @RequestPart(value = "file", required = false) List<MultipartFile> files,
            @CurrentUser User user
    ) throws IOException {

        if (bindingResult.hasErrors()) {
            throw new GameRootException(GameErrorMessages.GAME_POST_ERROR, HttpStatus.BAD_REQUEST);
        }

        GameResponseDto gameResponseDto = boardGameService.create(gamePostDto, files, user);
        CommonResponse<GameResponseDto> response = CommonResponse.<GameResponseDto>builder()
                .payload(gameResponseDto)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "게임 단건 조회", description = "게임 id로 게임 정보를 조회 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게임 조회 성공"),
            @ApiResponse(responseCode = "404", description = "삭제된 게임이나 존재하지 않는 게임 id"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{gameId}")
    public ResponseEntity<CommonResponse<GameResponseDto>> getGame(
            @PathVariable @Min(1) Long gameId,
            @RequestParam(required = false) boolean wantComments,
            @RequestParam(required = false) boolean wantPosts,
            @RequestParam(required = false) String  category
    ) {

        GameResponseDto foundGame = boardGameService.findGameByGameId(gameId, wantComments, wantPosts, category);

        CommonResponse<GameResponseDto> response = CommonResponse.<GameResponseDto>builder()
                .payload(foundGame)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "게임 삭제", description = "게임 id로 게임을 삭제합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게임 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "존재 하지 않거나 이미 삭제된 게임 삭제 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/{gameId}")
    public ResponseEntity<CommonResponse<?>> deleteGame(@PathVariable @Min(1) Long gameId) {

        boardGameService.deleteGameByGameId(gameId);

        return new ResponseEntity<>(
                CommonResponse.builder()
                        .status(200)
                        .message("게임을 삭제했어요")
                        .build()
                ,HttpStatus.OK);
    }

    @Operation(summary = "게임 수정", description = "게임 id로 게임을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게임 수정 성공"),
            @ApiResponse(responseCode = "400", description = "필수 입력값 누락등 검증 통과 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("/{gameId}")
    public ResponseEntity<CommonResponse<GameResponseDto>> putGame(
            @PathVariable Long gameId,
            @RequestPart("gamePutDto") @Validated GamePutDto gamePutDto,
            @RequestPart(value = "file", required = false) List<MultipartFile> files,
            BindingResult bindingResult,
            @CurrentUser User user
    ) throws  IOException {

        if (bindingResult.hasErrors()) {
            throw new GameRootException(GameErrorMessages.MISSING_REQUIRED_INPUT, HttpStatus.BAD_REQUEST);
        }

        GameResponseDto gameResponseDto = boardGameService.editGameV2(gamePutDto, files, user);
        CommonResponse<GameResponseDto> response = CommonResponse.<GameResponseDto>builder()
                .payload(gameResponseDto)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "게임 제목 검색", description = "제목으로 게임을 검색합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/search")
    public ResponseEntity<CommonResponse<Page<SearchResponse>>> searchByKeyword(@ModelAttribute SearchRequest searchRequest) {
        String keyword = searchRequest.getKeyword();
        Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize());
        Page<SearchResponse> foundGames = boardGameService.searchByKeyword(keyword, pageable);
        CommonResponse<Page<SearchResponse>> response = CommonResponse.<Page<SearchResponse>>builder()
            .payload(foundGames)
            .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "게임 좋아요", description = "좋아요, 좋아요 취소를 처리 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "좋아요, 좋아요 취소 성공"),
            @ApiResponse(responseCode = "400", description = "존재하지 않는 gameId, 혹은 요청 user 확인 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/like")
    public ResponseEntity<CommonResponse<ClickLikeResponseDto>> clickLike(@RequestParam @Min(1) Long gameId, @CurrentUser User user) {

        ClickLikeResponseDto clickLikeResponseDto = boardGameService.clickLike(gameId, user);
        CommonResponse<ClickLikeResponseDto> response = CommonResponse.<ClickLikeResponseDto>builder()
                .payload(clickLikeResponseDto)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "게임 평점", description = "평점등록, 수정, 취소를 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "평점 처리 성공"),
            @ApiResponse(responseCode = "400", description = "존재하지 않는 gameId, 혹은 요청 user 확인 실패, 정상 범위를 벗어난 평점 등록 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/rate")
    public ResponseEntity<CommonResponse<GameRateResponseDto>> postGameRate(
            @RequestParam @Min(1) Long gameId,
            @RequestBody @Validated GameRatePostDto gameRatePostDto,
            @CurrentUser User user) {

        GameRateResponseDto responseDto = boardGameService.clickGameRate(gameId, gameRatePostDto, user);
        CommonResponse<GameRateResponseDto> response = CommonResponse.<GameRateResponseDto>builder()
                .payload(responseDto)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "게임 리스트 조회", description = "게임 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게임 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public ResponseEntity<CommonResponse<Page<GameListResponseDto>>> getGames(
            @ModelAttribute GamesPaginationRequest paginationRequest,
            @RequestParam(required = false) String keyword
            ) {

        int page = paginationRequest.getPage() == 0 ? 0 : paginationRequest.getPage();
        int size = paginationRequest.getSize() == 0 ? 12 : paginationRequest.getSize();
        Enums.GameListSortOption sortBy = paginationRequest.getSortBy() == null ? Enums.GameListSortOption.GAME_ID : paginationRequest.getSortBy();

        Pageable pageable = PageRequest.of(page, size);

        Page<GameListResponseDto> gameListResponseDtoPage = boardGameService.findAll(pageable, sortBy, keyword);

        CommonResponse<Page<GameListResponseDto>> response = CommonResponse.<Page<GameListResponseDto>>builder()
                .payload(gameListResponseDtoPage)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "게임 조회수", description = "게임 조회수를 증가 시킵니다.")
    @PostMapping("/view")
    public ResponseEntity<Void> incrementViewCount(@RequestHeader("visitor-id") @NotBlank String visitorId, @RequestHeader("game-id") @Min(1) Long gameId) {
        boardGameService.incrementViewCount(visitorId, gameId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Home용 게임 5건씩 조회", description = "Home화면에서 사용하는 조건별 게임을 5건씩 조회 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게임 조회 성공"),
            @ApiResponse(responseCode = "400", description = "정렬을 위한 필수 쿼리스트링 누락"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/home")
    public ResponseEntity<CommonResponse<List<HomeGamesResponseDto>>> getHomeGames(
            @RequestParam @NotNull Enums.GameListSortOption sort,
            @RequestParam(required = false) String genre
            ) {

        List<HomeGamesResponseDto> homeGamesResponseDtos = boardGameService.findGamesByGenreAndSort(sort, genre);
        CommonResponse<List<HomeGamesResponseDto>> response = CommonResponse.<List<HomeGamesResponseDto>>builder()
                .payload(homeGamesResponseDtos)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Operation(summary = "게임 정보 최초 작성자 확인", description = "게임을 삭제할 수 있는 최초 작성자인지 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "확인 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/isFirstCreator")
    public ResponseEntity<Boolean> isFirstCreator(@RequestParam Long gameId, @CurrentUser User user) {

        Boolean result = boardGameService.checkFirstCreatorOrAdmin(gameId, user);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "게임 수정 내역 조회", description = "게임 정보 수정 내역을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정내역이 있고, 조회에 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 게임의 조회요청, 수정내역이 없는 게임의 내역 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{gameId}/histories")
    public ResponseEntity<CommonResponse<Page<GameHistoriesResponseDto>>> getGameHistories(Pageable pageable,@PathVariable Long gameId) {

        Page<GameHistoriesResponseDto> histories = boardGameService.getGameHistoriesByGameId(pageable,gameId);

        CommonResponse<Page<GameHistoriesResponseDto>> response = CommonResponse.<Page<GameHistoriesResponseDto>>builder()
                .payload(histories)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "특정 버전 게임 정보 조회", description = "특정 버전의 게임 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정내역이 있고, 조회에 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 게임의 조회요청, 존재하지 않는 수정내역 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/history/{historyId}")
    public ResponseEntity<CommonResponse<GameHistoryResponseDto>> getGameHistory(@PathVariable Long historyId) {

        GameHistoryResponseDto dto = boardGameService.getGameHistory(historyId);

        CommonResponse<GameHistoryResponseDto> response = CommonResponse.<GameHistoryResponseDto>builder()
                .payload(dto)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
