package com.elice.boardgame.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClickLikeResponseDto {

    private int likeCount;

//    private ClickLikeResponseMessages messages;
    private String messages;

    @Getter
    @AllArgsConstructor
    public enum ClickLikeResponseMessages {

        LIKE_ADDED("좋아요를 눌렀어요."),
        LIKE_REMOVED("좋아요가 취소되었어요.");

        private final String message;
    }
}
