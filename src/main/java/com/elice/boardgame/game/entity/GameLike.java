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
public class GameLike {

    @EmbeddedId
    private GameLikePK gameLikePK;

    @ManyToOne
    @MapsId("gameId")
    @JoinColumn(name = "game_id")
    private BoardGame boardGame;

    /*@ManyToOne
    @MapsId("memberId")
    @JoinColumn(name = "member_id")
    private Member member;*/
}
