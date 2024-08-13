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
    @Column(name = "pic_id")
    private Long picId;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private BoardGame boardGame;

    @Column(name = "pic_address")
    private String picAddress;

    @Column(name = "file_name")
    private String fileName;

    @ManyToOne
    @JoinColumn(name = "game_history_id")
    private BoardGameHistory gameHistory;

    @Column(name = "is_active")
    private Boolean isActive = true;
}
