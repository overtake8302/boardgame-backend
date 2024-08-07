package com.elice.boardgame.game.controller;

import com.elice.boardgame.common.dto.CommonResponse;
import com.elice.boardgame.common.dto.PaginationRequest;
import com.elice.boardgame.common.dto.SearchRequest;
import com.elice.boardgame.common.dto.SearchResponse;
import com.elice.boardgame.common.enums.Enums;
import com.elice.boardgame.common.exceptions.GameErrorMessages;
import com.elice.boardgame.common.exceptions.GameRootException;
import com.elice.boardgame.game.dto.*;
import com.elice.boardgame.game.mapper.BoardGameMapper;
import com.elice.boardgame.game.service.BoardGameService;
import com.elice.boardgame.post.dto.PostDto;
import com.elice.boardgame.post.entity.Comment;
import com.elice.boardgame.post.entity.Post;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    @PostMapping
    public ResponseEntity<CommonResponse<GameResponseDto>> postGame(
            @RequestPart("gamePostDto") @Validated GamePostDto gamePostDto,
            BindingResult bindingResult,
            @RequestPart(value = "file", required = false) List<MultipartFile> files
    ) throws IOException {

        if (bindingResult.hasErrors()) {
            throw new GameRootException(GameErrorMessages.GAME_POST_ERROR, HttpStatus.BAD_REQUEST);
        }

        GameResponseDto gameResponseDto = boardGameService.create(gamePostDto, files);
        CommonResponse<GameResponseDto> response = CommonResponse.<GameResponseDto>builder()
                .payload(gameResponseDto)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /*@GetMapping("/{gameId}")
    public ResponseEntity<CommonResponse<GameResponseDto>> getGame(@PathVariable @Min(1) Long gameId) {

        GameResponseDto foundGame = boardGameService.findGameByGameId(gameId);
        CommonResponse<GameResponseDto> response = CommonResponse.<GameResponseDto>builder()
                .payload(foundGame)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }*/

    @GetMapping("/{gameId}")
    public ResponseEntity<CommonResponse<GameResponseDto>> getGame(
            @PathVariable @Min(1) Long gameId,
            @RequestParam(required = false) boolean wantComments,
            @RequestParam(required = false) boolean wantPosts,
            @RequestParam(required = false) Enums.Category category
    ) {

        GameResponseDto foundGame = boardGameService.findGameByGameId(gameId);

        /*if (wantComments) {
            List<Comment> comments = boardGameService.findComentsByGameId(gameId);
        }*/

        if (wantPosts) {
            List<PostDto> posts = boardGameService.getTop10Posts(gameId,category);
            foundGame.setPosts(posts);
        }

        CommonResponse<GameResponseDto> response = CommonResponse.<GameResponseDto>builder()
                .payload(foundGame)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

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

    @PutMapping
    public ResponseEntity<CommonResponse<GameResponseDto>> putGame(
            @RequestPart("gamePutDto") @Validated GamePutDto gamePutDto,
            @RequestPart(value = "file", required = false) List<MultipartFile> files,
            BindingResult bindingResult
    ) throws  IOException {

        if (bindingResult.hasErrors()) {
            throw new GameRootException(GameErrorMessages.MISSING_REQUIRED_INPUT, HttpStatus.BAD_REQUEST);
        }

        GameResponseDto gameResponseDto = boardGameService.editGame(gamePutDto, files);
        CommonResponse<GameResponseDto> response = CommonResponse.<GameResponseDto>builder()
                .payload(gameResponseDto)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

//    @GetMapping("/search")
//    public ResponseEntity<CommonResponse<Page<GameResponseDto>>> searchByName(
//            @RequestParam(required = true) @NotBlank String keyword,
//            @PageableDefault(page = 0, size = 20) Pageable pageable
//            ) {
//
//        Page<GameResponseDto> foundGames = boardGameService.findGameByName(keyword, pageable);
//        CommonResponse<Page<GameResponseDto>> response = CommonResponse.<Page<GameResponseDto>>builder()
//                .payload(foundGames)
//                .build();
//
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }

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

    @PostMapping("/like")
    public ResponseEntity<CommonResponse<ClickLikeResponseDto>> clickLike(@RequestParam @Min(1) Long gameId) {

        ClickLikeResponseDto clickLikeResponseDto = boardGameService.clickLike(gameId);
        CommonResponse<ClickLikeResponseDto> response = CommonResponse.<ClickLikeResponseDto>builder()
                .payload(clickLikeResponseDto)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/rate")
    public ResponseEntity<CommonResponse<GameRateResponseDto>> postGameRate(
            @RequestParam @Min(1) Long gameId,
            @RequestBody @Validated GameRatePostDto gameRatePostDto) {

        GameRateResponseDto responseDto = boardGameService.clickGameRate(gameId, gameRatePostDto);
        CommonResponse<GameRateResponseDto> response = CommonResponse.<GameRateResponseDto>builder()
                .payload(responseDto)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<CommonResponse<Page<GameResponseDto>>> getGames(
            @ModelAttribute GamesPaginationRequest paginationRequest
            ) {

        int page = paginationRequest.getPage() == 0 ? 0 : paginationRequest.getPage();
        int size = paginationRequest.getSize() == 0 ? 12 : paginationRequest.getSize();
        Enums.GameListSortOption sortBy = paginationRequest.getSortBy() == null ? Enums.GameListSortOption.GAME_ID : paginationRequest.getSortBy();

        Pageable pageable = PageRequest.of(page, size);

        Page<GameResponseDto> gameResponseDtoPage = boardGameService.findAll(pageable, sortBy);

        CommonResponse<Page<GameResponseDto>> response = CommonResponse.<Page<GameResponseDto>>builder()
                .payload(gameResponseDtoPage)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("/view")
    public ResponseEntity<Void> incrementViewCount(@RequestHeader("visitorId") @NotBlank String visitorId, @RequestHeader("gameId") @Min(1) Long gameId) {
        boardGameService.incrementViewCount(visitorId, gameId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/home")
    public ResponseEntity<CommonResponse<List<GameResponseDto>>> getHomeGames(@RequestParam @NotBlank String genre, @RequestParam @NotNull Enums.GameListSortOption sort) {

        List<GameResponseDto> gameResponseDtos = boardGameService.findGamesByGenreAndSort(genre, sort);
        CommonResponse<List<GameResponseDto>> response = CommonResponse.<List<GameResponseDto>>builder()
                .payload(gameResponseDtos)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
