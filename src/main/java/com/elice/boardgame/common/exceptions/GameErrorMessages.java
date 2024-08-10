package com.elice.boardgame.common.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum GameErrorMessages {

    MISSING_REQUIRED_INPUT("E001", "Required input is missing"),
    INVALID_INPUT_FORMAT("E002", "Invalid input format"),
    GAME_NOT_FOUND("E003", "Game not found"),
    GAME_DELETE_FAIL("E004", "Game Delete fail"),
    GAME_POST_ERROR("E005", "Game Post fail"),
    ACCESS_DENIED("E006", "access denied"),
    NO_COMMENTS("E007", "게임과 관련된 댓글이 없어요."),
    UNAUTHORIZED("E008", "로그인이 필요해요."),
    IS_FIRST_CREATOR_FALSE("E009", "최초 작성자나 관리자만 삭제할 수 있어요."),
    YOUTUBE_LINK_ERROR("E010", "링크를 확인해 주세요.");

    private final String errorCode;
    private final String errorMessage;
}
