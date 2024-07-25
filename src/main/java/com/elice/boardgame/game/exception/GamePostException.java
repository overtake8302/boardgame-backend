package com.elice.boardgame.game.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GamePostException extends GameRootException {

    public GamePostException() {
        super(GameErrorMessages.MISSING_REQUIRED_INPUT);
    }
}
