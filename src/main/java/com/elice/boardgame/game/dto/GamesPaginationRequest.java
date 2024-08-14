    package com.elice.boardgame.game.dto;

    import com.elice.boardgame.common.dto.PaginationRequest;
    import com.elice.boardgame.common.enums.Enums;
    import lombok.AllArgsConstructor;
    import lombok.Getter;

    @Getter
    @AllArgsConstructor
    public class GamesPaginationRequest extends PaginationRequest {

        private final Enums.GameListSortOption sortBy;

    }
