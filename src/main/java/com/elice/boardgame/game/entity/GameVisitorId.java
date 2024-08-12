package com.elice.boardgame.game.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GameVisitorId implements Serializable {

    @Column(name = "visitorId")
    private String visitorId;

    @Column(name = "game_id")
    private Long gameId;
}
