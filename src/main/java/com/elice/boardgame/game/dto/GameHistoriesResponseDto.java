package com.elice.boardgame.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameHistoriesResponseDto {

    private Long gameId;

    private Long gameHistoryId;

    private String name;

    private LocalDateTime createdAt;

    private String editBy;
}
