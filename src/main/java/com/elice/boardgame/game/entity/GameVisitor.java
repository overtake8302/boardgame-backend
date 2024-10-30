package com.elice.boardgame.game.entity;

import com.elice.boardgame.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "game_visitor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameVisitor extends BaseEntity {

    @EmbeddedId
    private GameVisitorId id;

    @MapsId("gameId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private BoardGame boardGame;

    public GameVisitor(String visitorId, Long gameId) {
        this.id = new GameVisitorId(visitorId, gameId);
    }

}
