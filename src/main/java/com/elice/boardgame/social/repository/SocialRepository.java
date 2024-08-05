package com.elice.boardgame.social.repository;

import com.elice.boardgame.social.entity.Social;
import com.elice.boardgame.social.entity.SocialId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialRepository extends JpaRepository<Social, SocialId>, SocialRepositoryCustom {
}
