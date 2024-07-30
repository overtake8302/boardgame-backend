package com.elice.boardgame.social.repository;

import com.elice.boardgame.social.entity.Social;
import com.elice.boardgame.social.entity.SocialId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialRepository extends JpaRepository<Social, SocialId>, SocialRepositoryCustom {

    // 예외처리, 친구인 경우에만 다른 유저의 MyPage 볼 수 있도록
    boolean existsByUserIdAndFriendId(Long userId, Long friendId);

    void deleteByUserIdAndFriendId(Long userId, Long friendId);
}
