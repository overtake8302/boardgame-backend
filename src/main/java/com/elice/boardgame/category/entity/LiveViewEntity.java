package com.elice.boardgame.category.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import lombok.Data;

@Entity
@Table(name = "LiveView")
@Data
public class LiveViewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long liveViewId;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private BoardGameEntity game;

    @Temporal(TemporalType.DATE)
    private Date createdDate;

    private Long viewCount;

}
