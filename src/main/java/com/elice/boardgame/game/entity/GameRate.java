package com.elice.boardgame.game.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GameRate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rateId;

    /*@ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;*/

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private BoardGame boardGame;

    private Double rate;
}
