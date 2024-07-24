package com.elice.boardgame.game.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GamePostException extends GameRootException {

    public GamePostException(GameErrorMessages gameErrorMessages) {
        super(gameErrorMessages);
    }
}
