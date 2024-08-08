package com.elice.boardgame.post.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class CommentDto {
    private Long id;
    private String content;
    private Long userId;
    private String userName;
    private List<CommentDto> comInComs;
}