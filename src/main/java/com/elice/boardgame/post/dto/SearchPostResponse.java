package com.elice.boardgame.post.dto;

import com.elice.boardgame.common.dto.SearchResponse;
import com.elice.boardgame.common.enums.Enums;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchPostResponse extends SearchResponse {
    String category;

    public SearchPostResponse(Long id, String category, String name) {
        super(id, name);
        this.category = category;
    }
}
