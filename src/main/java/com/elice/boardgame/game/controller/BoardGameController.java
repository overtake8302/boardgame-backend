package com.elice.boardgame.game.controller;

import com.elice.boardgame.common.dto.CommonResponse;
import com.elice.boardgame.common.dto.PaginationRequest;
import com.elice.boardgame.common.exceptions.GameErrorMessages;
import com.elice.boardgame.common.exceptions.GameRootException;
import com.elice.boardgame.game.dto.*;
import com.elice.boardgame.game.mapper.BoardGameMapper;
import com.elice.boardgame.game.service.BoardGameService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
@RequestMapping("/api")
@Validated
@RequiredArgsConstructor
public class BoardGameController {

    private final BoardGameService boardGameService;
    private final BoardGameMapper mapper;

    @PostMapping("/game")
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

    @GetMapping("/game/{gameId}")
    public ResponseEntity<CommonResponse<GameResponseDto>> getGame(@PathVariable @Min(1) Long gameId) {

        GameResponseDto foundGame = boardGameService.findGameByGameId(gameId);
        CommonResponse<GameResponseDto> response = CommonResponse.<GameResponseDto>builder()
                .payload(foundGame)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/game/{gameId}")
    public ResponseEntity<CommonResponse<?>> deleteGame(@PathVariable @Min(1) Long gameId) {

        boardGameService.deleteGameByGameId(gameId);

        return new ResponseEntity<>(
                CommonResponse.builder()
                        .status(200)
                        .message("게임을 삭제했어요")
                        .build()
                ,HttpStatus.OK);
    }

    @PutMapping("/game/{gameId}")
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

    @GetMapping("/game/search")
    public ResponseEntity<CommonResponse<List<GameResponseDto>>> searchByName(@RequestParam(required = true) @NotBlank String keyword) {

        List<GameResponseDto> foundGames = boardGameService.findGameByName(keyword);
        CommonResponse<List<GameResponseDto>> response = CommonResponse.<List<GameResponseDto>>builder()
                .payload(foundGames)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/game/like")
    public ResponseEntity<CommonResponse<ClickLikeResponseDto>> clickLike(@RequestParam @Min(1) Long gameId) {

        ClickLikeResponseDto clickLikeResponseDto = boardGameService.clickLike(gameId);
        CommonResponse<ClickLikeResponseDto> response = CommonResponse.<ClickLikeResponseDto>builder()
                .payload(clickLikeResponseDto)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/game/rate")
    public ResponseEntity<CommonResponse<GameRateResponseDto>> postGameRate(
            @RequestParam @Min(1) Long gameId,
            @RequestBody @Validated GameRatePostDto gameRatePostDto) {

        GameRateResponseDto responseDto = boardGameService.clickGameRate(gameId, gameRatePostDto);
        CommonResponse<GameRateResponseDto> response = CommonResponse.<GameRateResponseDto>builder()
                .payload(responseDto)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/games")
    public ResponseEntity<CommonResponse<Page<GameResponseDto>>> getGames(
            @ModelAttribute PaginationRequest paginationRequest,
            @RequestParam(defaultValue = "gameId") @NotBlank String sortBy) {

        int page = paginationRequest.getPage() == 0 ? 0 : paginationRequest.getPage();
        int size = paginationRequest.getSize() == 0 ? 12 : paginationRequest.getSize();

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        Page<GameResponseDto> gameResponseDtoPage = boardGameService.findAll(pageable);

        CommonResponse<Page<GameResponseDto>> response = CommonResponse.<Page<GameResponseDto>>builder()
                .payload(gameResponseDtoPage)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("/game/view")
    public ResponseEntity<Void> incrementViewCount(@RequestHeader("visitorId") @NotBlank String visitorId, @RequestHeader("gameId") @Min(1) Long gameId) {
        boardGameService.incrementViewCount(visitorId, gameId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/game/home")
    public ResponseEntity<CommonResponse<List<GameResponseDto>>> getHomeGames(@RequestParam @NotBlank String genre, @RequestParam @NotBlank String sort) {

        List<GameResponseDto> gameResponseDtos = boardGameService.findGamesByGenreAndSort(genre, sort);
        CommonResponse<List<GameResponseDto>> response = CommonResponse.<List<GameResponseDto>>builder()
                .payload(gameResponseDtos)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
