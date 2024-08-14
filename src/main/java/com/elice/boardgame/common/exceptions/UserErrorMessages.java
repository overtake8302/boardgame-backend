package com.elice.boardgame.common.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserErrorMessages {
    USER_NOT_FOUND("E001", "해당 유저를 찾을 수 없어요. 로그인 하지 않았거나, 탈퇴한 회원, 존재하지 않는 회원이에요."),
    USER_POSTS_NOT_FOUND("E002", "작성한 게시글이 없어요."),
    USER_COMMENTS_NOT_FOUNT("E003", "작성한 댓글이 없어요.");

    private final String errorCode;
    private final String errorMessage;
}
