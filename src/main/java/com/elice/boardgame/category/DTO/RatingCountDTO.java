package com.elice.boardgame.category.DTO;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class RatingCountDTO {
    private Double rate;
    private Long count;
}
