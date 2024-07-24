package com.elice.boardgame.game.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GameExceptionController {

    @ExceptionHandler(GamePostException.class)
    public ResponseEntity<GameErrorResponse> handleGamePostException(GamePostException gamePostException) {

        GameErrorResponse errorResponse = new GameErrorResponse(gamePostException.getErrorMessage().getErrorCode(), gamePostException.getErrorMessage().getErrorMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
