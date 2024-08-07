package com.elice.boardgame.game.dto;

import com.elice.boardgame.category.entity.GameGenre;
import com.elice.boardgame.post.dto.PostDto;
import com.elice.boardgame.post.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GameResponseDto {

    private Long gameId;

    private String name;

    private String playTime;

    private String playNum;

    private String ageLimit;

    private int price;

    private String designer;

    private String artwork;

    private String releaseDate;

    private String difficulty;

    private String publisher;

    private String youtubeLink;

    private int likeCount;

    private Double averageRate;

    private List<String> gameProfilePics;

    private List<GameGenre> gameGenres = new ArrayList<>();

    private Long views;

    private List<PostDto> posts;

}
