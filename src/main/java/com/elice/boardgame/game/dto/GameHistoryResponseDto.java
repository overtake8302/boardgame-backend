package com.elice.boardgame.game.dto;

import com.elice.boardgame.category.entity.GameGenre;
import com.elice.boardgame.game.entity.GameGenreHistory;
import com.elice.boardgame.post.dto.CommentDto;
import com.elice.boardgame.post.dto.PostDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GameHistoryResponseDto {

    private Long gameHistoryId;

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

    private List<String> gameProfilePics;

    private List<GameGenreHistory> gameGenres = new ArrayList<>();

    private LocalDateTime createdAt;

    private String creatorName;

}
