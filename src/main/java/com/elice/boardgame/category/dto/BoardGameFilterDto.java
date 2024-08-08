package com.elice.boardgame.category.dto;

import java.util.List;
import lombok.Data;

@Data
public class BoardGameFilterDto {
    private List<String> playTimes;
    private List<String> playNums;
    private List<String> ageLimits;
    private List<String> prices;
    private List<String> genres;
}
