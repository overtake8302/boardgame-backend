package com.elice.boardgame.social.repository;

import com.elice.boardgame.social.entity.Social;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SocialRepositoryCustom {
    Page<Long> findFriendIdsByUserId(Long userId, Pageable pageable);
    boolean existsByUserIdAndFriendId(Long userId, Long friendId);
    void deleteByUserIdAndFriendIdBothWays(Long userId, Long friendId);
    Social save(Social social);
}
