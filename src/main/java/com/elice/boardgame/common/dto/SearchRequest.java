package com.elice.boardgame.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchRequest extends PaginationRequest{
    private String keyword;
}
