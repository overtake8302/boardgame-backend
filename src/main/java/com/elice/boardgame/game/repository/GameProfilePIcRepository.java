package com.elice.boardgame.game.repository;

import com.elice.boardgame.game.entity.GameProfilePic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameProfilePIcRepository extends JpaRepository<GameProfilePic, Long> {
    void deleteByPicId(Long picId);
}
