package com.elice.boardgame.category.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostListResponseDto {
    private Long postId;
    private String postType;
    private String title;
    private String writer;
    private LocalDateTime createdAt;
    private Long views;
    private Long likes;
    private Long comments;
}
