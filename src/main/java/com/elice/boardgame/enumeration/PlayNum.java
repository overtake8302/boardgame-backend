package com.elice.boardgame.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PlayNum {
    ONE_PLAYER("1인용"),
    TWO_PLAYERS("2인용"),
    THREE_PLAYERS("3인용"),
    FOUR_PLAYERS("4인용"),
    FIVE_PLUS_PLAYERS("5인 이상");

    private final String label;
}
