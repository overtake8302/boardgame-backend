package com.elice.boardgame.game.exception;

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
