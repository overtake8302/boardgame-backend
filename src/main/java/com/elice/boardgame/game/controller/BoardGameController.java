package com.elice.boardgame.game.controller;

import com.elice.boardgame.game.dto.GamePostDto;
import com.elice.boardgame.game.dto.GameResponseDto;
import com.elice.boardgame.game.entity.BoardGame;
import com.elice.boardgame.game.exception.GameErrorMessages;
import com.elice.boardgame.game.exception.GamePostException;
import com.elice.boardgame.game.mapper.BoardGameMapper;
import com.elice.boardgame.game.service.BoardGameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/game")
@Validated
@RequiredArgsConstructor
public class BoardGameController {

    private final BoardGameService boardGameService;
    private final BoardGameMapper mapper;

    @PostMapping
    public ResponseEntity<GameResponseDto> postGame(@RequestBody @Validated GamePostDto gamePostDto, BindingResult bindingResult, @RequestParam(value = "file", required = false) List<MultipartFile> files) throws IOException {

        if (bindingResult.hasErrors()) {
            throw new GamePostException(GameErrorMessages.MISSING_REQUIRED_INPUT);
        }

        BoardGame newBoardGame = mapper.gamePostDtoToBoardGame(gamePostDto);

        BoardGame savedBoardGame = new BoardGame();

        if (files != null && !files.isEmpty()) {
            savedBoardGame = boardGameService.create(newBoardGame, files);
        } else {
            savedBoardGame = boardGameService.create(newBoardGame);
        }

        GameResponseDto gameResponseDto = mapper.boardGameToGameResponseDto(savedBoardGame);

        return new ResponseEntity<>(gameResponseDto, HttpStatus.CREATED);
    }
}
