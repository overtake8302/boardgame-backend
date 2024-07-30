package com.elice.boardgame.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AgeLimit {

    AGE_ALL("전체 이용가"),
    AGE_12_PLUS("12세 이용가"),
    AGE_15_PLUS("15세 이용가"),
    AGE_18_PLUS("청소년 이용 불가");

    private final String label;
}
