package com.elice.boardgame.game.entity;

import com.elice.boardgame.auth.entity.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

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

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "created_date", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDate;
}

