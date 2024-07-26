package com.elice.boardgame.category.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "BoardGame")
@Data
public class BoardGameEntity {

    @Id
    private Long gameId;
}
