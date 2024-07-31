package com.elice.boardgame.game.controller;

import com.elice.boardgame.ExceptionHandler.GameErrorMessages;
import com.elice.boardgame.ExceptionHandler.GameRootException;
import com.elice.boardgame.game.dto.*;
import com.elice.boardgame.game.entity.BoardGame;
import com.elice.boardgame.game.mapper.BoardGameMapper;
import com.elice.boardgame.game.service.BoardGameService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@Validated
@RequiredArgsConstructor
public class BoardGameController {

    private final BoardGameService boardGameService;
    private final BoardGameMapper mapper;

    @PostMapping("/game")
    public ResponseEntity<GameResponseDto> postGame(
            @RequestPart("gamePostDto") @Validated GamePostDto gamePostDto,
            BindingResult bindingResult,
            @RequestPart(value = "file", required = false) List<MultipartFile> files
    ) throws IOException {

        if (bindingResult.hasErrors()) {
            throw new GameRootException(GameErrorMessages.GAME_POST_ERROR);
        }

        GameResponseDto gameResponseDto = new GameResponseDto();
        if (files != null && !files.isEmpty()) {
            gameResponseDto = boardGameService.create(gamePostDto, files);
        } else {
            gameResponseDto = boardGameService.create(gamePostDto);
        }

        return new ResponseEntity<>(gameResponseDto, HttpStatus.CREATED);
    }

    @GetMapping("/game/{gameId}")
    public ResponseEntity<GameResponseDto> getGame(@PathVariable Long gameId) {

        BoardGame foundGame = boardGameService.findGameByGameId(gameId);

        GameResponseDto gameResponseDto = mapper.boardGameToGameResponseDto(foundGame);

        return new ResponseEntity<>(gameResponseDto, HttpStatus.OK);
    }

    @DeleteMapping("/game/{gameId}")
    public ResponseEntity<HttpStatus> deleteGame(@PathVariable Long gameId) {

        boardGameService.deleteGameByGameId(gameId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/game/{gameId}")
    public ResponseEntity<GameResponseDto> putGame(
            @RequestPart("gamePutDto") @Validated GamePutDto gamePutDto,
            @RequestPart(value = "file", required = false) List<MultipartFile> files,
            BindingResult bindingResult
    ) throws  IOException {

        if (bindingResult.hasErrors()) {
            throw new GameRootException(GameErrorMessages.MISSING_REQUIRED_INPUT);
        }

        GameResponseDto gameResponseDto;

        if (files == null || files.isEmpty()) {
            gameResponseDto = boardGameService.editWithoutPics(gamePutDto);
        } else {
            gameResponseDto = boardGameService.editWithPics(gamePutDto, files);
        }

        return new ResponseEntity<>(gameResponseDto, HttpStatus.OK);
    }

    @GetMapping("/game/search")
    public ResponseEntity<List<GameResponseDto>> searchByName(@RequestParam String keyword) {

        List<BoardGame> foundGames = boardGameService.findGameByName(keyword);

        List<GameResponseDto> gameResponseDtos = new ArrayList<>();

        for (BoardGame boardGame : foundGames) {
            GameResponseDto dto = mapper.boardGameToGameResponseDto(boardGame);
            gameResponseDtos.add(dto);
        }

        return new ResponseEntity<>(gameResponseDtos, HttpStatus.OK);
    }

    @PostMapping("/game/like")
    public ResponseEntity<ClickLikeResponseDto> clickLike(@RequestParam Long gameId) {

        ClickLikeResponseDto clickLikeResponseDto = boardGameService.clickLike(gameId);

        return new ResponseEntity<>(clickLikeResponseDto, HttpStatus.OK);
    }

    @PostMapping("/game/rate")
    public ResponseEntity<GameRateResponseDto> postGameRate(@RequestParam Long gameId, @RequestBody GameRatePostDto gameRatePostDto) {

        GameRateResponseDto responseDto = boardGameService.clickGameRate(gameId, gameRatePostDto);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/games")
    public ResponseEntity<Page<GameResponseDto>> getGames(@PageableDefault(size = 10, page = 0) Pageable pageable, @RequestParam(defaultValue = "createdDate") String sortBy) {

        return new ResponseEntity<>(boardGameService.findAll(pageable, sortBy), HttpStatus.OK);
    }

    @PostMapping("/game/view")
    public ResponseEntity<Void> incrementViewCount(@RequestHeader("visitorId") String visitorId, @RequestHeader("gameId") Long gameId) {
        boardGameService.incrementViewCount(visitorId, gameId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
