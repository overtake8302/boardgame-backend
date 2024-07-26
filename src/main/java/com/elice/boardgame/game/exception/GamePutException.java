package com.elice.boardgame.game.exception;

public class GamePutException extends GameRootException {

    public GamePutException() {
        super(GameErrorMessages.MISSING_REQUIRED_INPUT);
    }
}
