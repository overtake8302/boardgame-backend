package com.elice.boardgame.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyCommentResponseDto {

    private String content;
    private Long id;
    private String createdAt;
    private Long postId;
}
