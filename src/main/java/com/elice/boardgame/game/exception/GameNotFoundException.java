package com.elice.boardgame.game.exception;

public class GameNotFoundException extends GameRootException {

    public GameNotFoundException() {
        super(GameErrorMessages.GAME_NOT_FOUND);
    }
}
