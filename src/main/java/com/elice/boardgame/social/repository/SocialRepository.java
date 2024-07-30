package com.elice.boardgame.social.repository;

import com.elice.boardgame.social.entity.Social;
import java.util.List;

public interface SocialRepository {
    List<Long> findFriendIdsByUserId(Long userId);
    boolean existsByUserIdAndFriendId(Long userId, Long friendId);
    void deleteByUserIdAndFriendIdBothWays(Long userId, Long friendId);
    Social save(Social social);
}
