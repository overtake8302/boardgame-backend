package com.elice.boardgame.post.dto;

import com.elice.boardgame.common.dto.SearchResponse;
import com.elice.boardgame.common.enums.Enums;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchPostResponse extends SearchResponse {
    Enums.Category category;

    public SearchPostResponse(Long id, Enums.Category category, String name) {
        super(id, name);
        this.category = category;
    }
}
