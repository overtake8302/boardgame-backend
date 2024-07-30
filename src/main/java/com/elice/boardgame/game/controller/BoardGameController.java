package com.elice.boardgame.game.controller;

import com.elice.boardgame.auth.repository.UserRepository;
import com.elice.boardgame.game.dto.ClickLikeResponseDto;
import com.elice.boardgame.game.dto.GamePostDto;
import com.elice.boardgame.game.dto.GamePutDto;
import com.elice.boardgame.game.dto.GameResponseDto;
import com.elice.boardgame.game.entity.BoardGame;
import com.elice.boardgame.ExceptionHandler.GameErrorMessages;
import com.elice.boardgame.ExceptionHandler.GameRootException;
import com.elice.boardgame.game.mapper.BoardGameMapper;
import com.elice.boardgame.game.repository.GameLikeRepository;
import com.elice.boardgame.game.service.BoardGameService;
import com.elice.boardgame.game.service.GameProfilePicService;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/game")
@Validated
@RequiredArgsConstructor
public class BoardGameController {

    private final BoardGameService boardGameService;
    private final BoardGameMapper mapper;
    private final GameProfilePicService gameProfilePicService;
    private final UserRepository userRepository;
    private final GameLikeRepository gameLikeRepository;

    @PostMapping
    public ResponseEntity<GameResponseDto> postGame(
            @RequestPart("gamePostDto") @Validated GamePostDto gamePostDto,
            BindingResult bindingResult,
            @RequestPart(value = "file", required = false) List<MultipartFile> files
    ) throws IOException {

        if (bindingResult.hasErrors()) {
            throw new GameRootException(GameErrorMessages.GAME_POST_ERROR);
        }

        BoardGame newBoardGame = mapper.gamePostDtoToBoardGame(gamePostDto);

        BoardGame savedBoardGame = new BoardGame();

        if (files != null && !files.isEmpty()) {
            savedBoardGame = boardGameService.create(newBoardGame, files, gamePostDto.getGameGenreIds());
        } else {
            savedBoardGame = boardGameService.create(newBoardGame, gamePostDto.getGameGenreIds());
        }

        GameResponseDto gameResponseDto = mapper.boardGameToGameResponseDto(savedBoardGame);

        return new ResponseEntity<>(gameResponseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<GameResponseDto> getGame(@PathVariable Long gameId) {

        BoardGame foundGame = boardGameService.findGameByGameId(gameId);

        GameResponseDto gameResponseDto = mapper.boardGameToGameResponseDto(foundGame);

        return new ResponseEntity<>(gameResponseDto, HttpStatus.OK);
    }

    @DeleteMapping("/{gameId}")
    public ResponseEntity<HttpStatus> deleteGame(@PathVariable Long gameId) {

        boardGameService.deleteGameByGameId(gameId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{gameId}")
    public ResponseEntity<GameResponseDto> putGame(
            @RequestPart("gamePutDto") @Validated GamePutDto gamePutDto,
            @RequestPart(value = "file", required = false) List<MultipartFile> files,
            BindingResult bindingResult
    ) throws  IOException {

        if (bindingResult.hasErrors()) {
            throw new GameRootException(GameErrorMessages.MISSING_REQUIRED_INPUT);
        }


        BoardGame target = mapper.gamePutDtoToBoardGame(gamePutDto);

        BoardGame updatedTarget;

        if (files == null || files.isEmpty()) {
            updatedTarget = boardGameService.editWithoutPics(target, gamePutDto.getGameGenreIds());
        } else {
            updatedTarget = boardGameService.editWithPics(target, files, gamePutDto.getGameGenreIds());
        }

        GameResponseDto gameResponseDto = mapper.boardGameToGameResponseDto(updatedTarget);

        return new ResponseEntity<>(gameResponseDto, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<GameResponseDto>> searchByName(@RequestParam String keyword) {

        List<BoardGame> foundGames = boardGameService.findGameByName(keyword);

        List<GameResponseDto> gameResponseDtos = new ArrayList<>();

        for (BoardGame boardGame : foundGames) {
            GameResponseDto dto = mapper.boardGameToGameResponseDto(boardGame);
            gameResponseDtos.add(dto);
        }

        return new ResponseEntity<>(gameResponseDtos, HttpStatus.OK);
    }

    @PostMapping("/like")
    public ResponseEntity<ClickLikeResponseDto> clickLike(@RequestParam Long gameId) {

        boolean like = boardGameService.clickLike(gameId);
        ClickLikeResponseDto clickLikeResponseDto = new ClickLikeResponseDto();

        if (like) {
            clickLikeResponseDto.setMessages(ClickLikeResponseDto.ClickLikeResponseMessages.LIKE_REMOVED.getMessage());
        } else {
            clickLikeResponseDto.setMessages(ClickLikeResponseDto.ClickLikeResponseMessages.LIKE_ADDED.getMessage());
        }

        int likeCount = gameLikeRepository.countLikesByBoardGameGameId(gameId);
        clickLikeResponseDto.setLikeCount(likeCount);

        return new ResponseEntity<>(clickLikeResponseDto, HttpStatus.OK);
    }
}
