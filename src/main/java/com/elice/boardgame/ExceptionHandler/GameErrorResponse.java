package com.elice.boardgame.ExceptionHandler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GameErrorResponse {

    private final String errorCode;
    private final String errorMessage;
}
