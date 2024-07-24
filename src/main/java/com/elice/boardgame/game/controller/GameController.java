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

@RestController
@RequestMapping("/api/game")
@Validated
@RequiredArgsConstructor
public class GameController {

    private final BoardGameService boardGameService;
    private final BoardGameMapper mapper;

    @PostMapping
    public ResponseEntity<GameResponseDto> postGame(@RequestBody @Validated GamePostDto gamePostDto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new GamePostException(GameErrorMessages.MISSING_REQUIRED_INPUT);
        }

        BoardGame newBoardGame = mapper.gamePostDtoToBoardGame(gamePostDto);

        BoardGame savedBoardGame = boardGameService.create(newBoardGame);

        GameResponseDto gameResponseDto = mapper.boardGameToGameResponseDto(savedBoardGame);

        return new ResponseEntity<>(gameResponseDto, HttpStatus.CREATED);
    }
}
