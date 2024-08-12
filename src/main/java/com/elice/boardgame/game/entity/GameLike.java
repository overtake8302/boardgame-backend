package com.elice.boardgame.game.entity;

import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "game_like")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameLike extends BaseEntity {

    @EmbeddedId
    private GameLikePK gameLikePK;

    @ManyToOne
    @MapsId("gameId")
    @JoinColumn(name = "game_id")
    private BoardGame boardGame;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;
}

