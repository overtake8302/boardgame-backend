package com.elice.boardgame.social.repository;

import java.util.List;

public interface SocialRepositoryCustom {
    List<Long> findFriendIdsByUserId(Long userId);
    boolean existsByUserIdAndFriendId(Long userId, Long friendId);
    void deleteByUserIdAndFriendIdBothWays(Long userId, Long friendId);
}
