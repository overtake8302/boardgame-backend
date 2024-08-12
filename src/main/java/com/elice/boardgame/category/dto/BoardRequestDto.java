package com.elice.boardgame.category.dto;

import lombok.Data;

@Data
public class BoardRequestDto {
    private int page;
    private int size;
    private String sortBy;
    private String boardType;
    private String query;
}
