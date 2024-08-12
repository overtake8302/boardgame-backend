package com.elice.boardgame.game.entity;

import com.elice.boardgame.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "game_profile_pic")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GameProfilePic extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long picId;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private BoardGame boardGame;

    private String picAddress;

    private String fileName;
}
