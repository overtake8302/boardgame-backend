package com.elice.boardgame.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyPostResponseDto {

    private Long postId;
    private String title;
    private String content;
    private String category;
    private String createdAt;
    private Long likeCount;
}
