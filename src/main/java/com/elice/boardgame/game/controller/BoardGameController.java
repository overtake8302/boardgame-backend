package com.elice.boardgame.game.controller;

import com.elice.boardgame.category.entity.GameGenre;
import com.elice.boardgame.game.dto.GamePostDto;
import com.elice.boardgame.game.dto.GamePutDto;
import com.elice.boardgame.game.dto.GameResponseDto;
import com.elice.boardgame.game.entity.BoardGame;
import com.elice.boardgame.game.exception.GamePostException;
import com.elice.boardgame.game.exception.GamePutException;
import com.elice.boardgame.game.mapper.BoardGameMapper;
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

    @PostMapping
    public ResponseEntity<GameResponseDto> postGame(@RequestPart("gamePostDto") @Validated GamePostDto gamePostDto, BindingResult bindingResult, @RequestPart(value = "file", required = false) List<MultipartFile> files) throws IOException {

        if (bindingResult.hasErrors()) {
            throw new GamePostException();
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
    public ResponseEntity<GameResponseDto> putGame(@RequestPart("gamePutDto") @Validated GamePutDto gamePutDto, @RequestPart(value = "file", required = false) List<MultipartFile> files, BindingResult bindingResult) throws  IOException {

        if (bindingResult.hasErrors()) {
            throw new GamePutException();
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
}
