package com.elice.boardgame.game.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameVisitor {

    @EmbeddedId
    private GameVisitorId id;

    /*@MapsId("gameId")
    @ManyToOne
    @JoinColumn(name = "gameId")
    private BoardGame boardGame;*/

    public GameVisitor(String visitorId, Long gameId) {
        this.id = new GameVisitorId(visitorId, gameId);
    }

}
