package com.elice.boardgame.common.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserErrorResponse {

    private final String errorCode;
    private final String errorMessage;
}
