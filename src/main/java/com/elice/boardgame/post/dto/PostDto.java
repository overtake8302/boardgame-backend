package com.elice.boardgame.post.dto;

import com.elice.boardgame.common.enums.Enums;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
public class PostDto {
    private Long postId;
    private String title;
    private String content;
    private Enums.Category category;
    private String imageUrl;
    private String imageName;
    private Long userId;
    private String userName;
    private List<CommentDto> comments;
    private LocalDateTime createdAt;
}