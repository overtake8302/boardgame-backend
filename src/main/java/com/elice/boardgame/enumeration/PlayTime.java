package com.elice.boardgame.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PlayTime {
    SHORT("30분 이하"),
    MEDIUM("30분 ~ 1시간"),
    LONG("1시간 이상");

    private final String label;
}
