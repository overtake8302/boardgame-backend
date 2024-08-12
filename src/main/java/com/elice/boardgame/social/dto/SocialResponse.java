package com.elice.boardgame.social.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SocialResponse {
    private Long id;
    private String name;

    public SocialResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
