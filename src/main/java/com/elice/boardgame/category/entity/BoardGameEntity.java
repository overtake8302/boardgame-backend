package com.elice.boardgame.category.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "BoardGame")
public class BoardGameEntity {

    @Id
    private Long gameId;
}
