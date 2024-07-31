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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String visitorId;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private BoardGame boardGame;

    public GameVisitor(String visitorId, BoardGame boardGame) {
        this.visitorId = visitorId;
        this.boardGame = boardGame;
    }
}
