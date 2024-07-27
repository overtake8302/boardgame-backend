package com.elice.boardgame.social.dto;

import lombok.Data;

@Data
public class SocialRequest {
    private Long userId;
    private Long friendId;
}
