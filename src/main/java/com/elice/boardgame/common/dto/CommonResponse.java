package com.elice.boardgame.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommonResponse<P> {
    private P payload;
    private String message;
    private int status;
}
