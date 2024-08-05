package com.elice.boardgame.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GameRateResponseMessages {

    EDITED("평점이 수정되었어요."),
    REGISTERED("평점이 등록되었어요.");

    private final String message;
}
