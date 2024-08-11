package com.elice.boardgame.game.dto;

import com.elice.boardgame.category.entity.GameGenre;
import com.elice.boardgame.post.dto.CommentDto;
import com.elice.boardgame.post.dto.PostDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GameListResponseDto {

    private Long gameId;

    private String name;

    private String difficulty;

    private int likeCount;

    private Double averageRate;

    private List<String> gameProfilePics;

    private Long views;
}
