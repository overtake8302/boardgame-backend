package com.elice.boardgame.category.dto;

import java.util.List;
import lombok.Data;

@Data
public class RatingCountDto {
    private Long count;
    private List<String> names;

    public RatingCountDto(Long count, List<String> names) {
        this.count = count;
        this.names = names;
    }
}
