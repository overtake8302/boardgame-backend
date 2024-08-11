package com.elice.boardgame.category.dto;

import java.util.List;
import lombok.Data;

@Data
public class RecentlyViewGameDto {

    private Long gameId;

    private List<String> gameProfilePics;
}
