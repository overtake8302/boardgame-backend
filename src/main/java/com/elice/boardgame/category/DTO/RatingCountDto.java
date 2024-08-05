package com.elice.boardgame.category.DTO;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RatingCountDto {
    private Long count;
    private List<String> names;
}
