package com.elice.boardgame.game.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostsByGame {

    private Long postId;
    private String category;
    private String title;
    private String content;
    private String createdAt;
}
