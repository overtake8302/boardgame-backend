package com.elice.boardgame.social.repository;

import java.util.List;

public interface SocialRepositoryCustom {
    List<Long> findFriendIdsByUserId(Long userId);
}
