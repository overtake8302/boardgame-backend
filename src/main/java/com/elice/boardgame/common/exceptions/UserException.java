package com.elice.boardgame.common.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class UserException extends RuntimeException{

    private final UserErrorMessages userErrorMessages;
    private final HttpStatus httpStatus;
}
