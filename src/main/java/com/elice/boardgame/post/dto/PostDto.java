package com.elice.boardgame.post.dto;

import com.elice.boardgame.common.enums.Enums;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PostDto {
    private String title;
    private String content;
    private Enums.Category category;
    private String imageUrl;
    private String imageName;
    private Long userId;
    private String userName;
    private List<CommentDto> comments;
}