package com.elice.boardgame.game.exception;

public class GameDeleteFailException extends GameRootException {

    public GameDeleteFailException() {
        super(GameErrorMessages.GAME_DELETE_FAIL);
    }
}
