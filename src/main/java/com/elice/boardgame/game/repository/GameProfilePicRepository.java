package com.elice.boardgame.game.repository;

import com.elice.boardgame.game.entity.GameProfilePic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameProfilePicRepository extends JpaRepository<GameProfilePic, Long> , CustomGameProfilePicRepository{

}
