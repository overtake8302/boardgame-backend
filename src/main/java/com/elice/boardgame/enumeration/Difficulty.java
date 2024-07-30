package com.elice.boardgame.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Difficulty {

    EASY("쉬움"),
    MEDIUM("보통"),
    HARD("어려움");

    private final String label;
}
