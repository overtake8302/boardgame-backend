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

    @ExceptionHandler(GameNotFoundException.class)
    public ResponseEntity<GameErrorResponse> handleGameNotFoundException(GameNotFoundException gameNotFoundException) {

        GameErrorResponse gameErrorResponse = new GameErrorResponse(gameNotFoundException.getErrorMessage().getErrorCode(), gameNotFoundException.getErrorMessage().getErrorMessage());

        return new ResponseEntity<>(gameErrorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(GameDeleteFailException.class)
    public ResponseEntity<GameErrorResponse> handleGameDeleteFailException(GameDeleteFailException e) {

        GameErrorResponse errorResponse = new GameErrorResponse(e.getErrorMessage().getErrorCode(), e.getErrorMessage().getErrorMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(GamePutException.class)
    public ResponseEntity<GameErrorResponse> handleGamePutException(GamePutException e) {

        GameErrorResponse errorResponse = new GameErrorResponse(e.getErrorMessage().getErrorCode(), e.getErrorMessage().getErrorMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
