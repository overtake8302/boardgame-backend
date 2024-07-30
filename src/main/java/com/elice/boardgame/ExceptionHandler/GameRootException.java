package com.elice.boardgame.ExceptionHandler;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class GameRootException extends RuntimeException{

    private final GameErrorMessages errorMessage;


}
