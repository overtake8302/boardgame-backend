package com.elice.boardgame.game.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum GameErrorMessages {

    MISSING_REQUIRED_INPUT("E001", "Required input is missing"),
    INVALID_INPUT_FORMAT("E002", "Invalid input format"),
    GAME_NOT_FOUND("E003", "Game not found");

    private final String errorCode;
    private final String errorMessage;
}
