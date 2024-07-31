package com.elice.boardgame.ExceptionHandler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
@AllArgsConstructor
public class GameRootException extends RuntimeException{

    private final GameErrorMessages errorMessage;

    private final HttpStatus httpStatus;
}
