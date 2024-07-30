package com.elice.boardgame.category.DTO;

import java.util.List;
import lombok.Data;

@Data
public class BoardGameFilterDTO {
    private List<String> playTimes;
    private List<String> playNums;
    private List<String> ageLimits;
    private List<String> prices;
    private List<String> genres;
}
