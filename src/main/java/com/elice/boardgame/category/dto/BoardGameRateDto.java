package com.elice.boardgame.category.dto;

import com.elice.boardgame.game.entity.BoardGame;
import lombok.Data;

@Data
public class BoardGameRateDto {
    private BoardGame boardGame;
    private Double rate;
}
