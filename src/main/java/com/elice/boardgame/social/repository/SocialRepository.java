package com.elice.boardgame.social.repository;

import com.elice.boardgame.social.entity.Social;
import com.elice.boardgame.social.entity.SocialId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialRepository extends JpaRepository<Social, SocialId> {
    List<Social> findByUserId(Long userId);

    @Query("SELECT s.id.friendId FROM Social s WHERE s.id.userId = :userId")
    List<Long> findFriendIdsByUserId(Long userId);

    boolean existsByUserIdAndFriendId(Long userId, Long friendId); // 친구인 경우 MyPage 볼 수 있도록

    void deleteByUserIdAndFriendId(Long userId, Long friendId);
}
