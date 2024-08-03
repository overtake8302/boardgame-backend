package com.elice.boardgame.category.DTO;

import com.elice.boardgame.game.entity.BoardGame;
import lombok.Data;

@Data
public class BoardGameRateDto {
    private BoardGame boardGame;
    private Double rate;
}
