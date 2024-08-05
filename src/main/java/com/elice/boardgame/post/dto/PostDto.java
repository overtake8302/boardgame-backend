package com.elice.boardgame.post.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PostDto {
    private String content;
    private String title;
    private String category;
    private List<String> imageUrls = new ArrayList<>();
    private List<String> imageNames = new ArrayList<>();
    private Long userId;
    private String userName;
    private List<CommentDto> comments;
    private String gameName;
    private Long gameId;
}