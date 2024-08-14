package com.elice.boardgame.game.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameCommentDto {

    private Long id;
    private Long userId;
    private String content;
    private String userName;
    private Long postId;
}
