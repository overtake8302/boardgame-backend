package com.elice.boardgame.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationRequest {
    private int page;
    private int size;
}
