package com.elice.boardgame.post.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.SimpleTimeZone;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PostDto {
    private Long postId;
    private String content;
    private String title;
    private String category;
    private Long viewCount;
    private Long userId;
    private String userName;
    private List<CommentDto> comments;
    private String gameName;
    private Long gameId;
    private String gameImageUrl;
    private String createdAt;
    private Long likeCount;
    private String userImageUrl;
}