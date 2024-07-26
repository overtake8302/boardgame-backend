package com.elice.boardgame.social.repository;

import com.elice.boardgame.social.entity.Social;
import com.elice.boardgame.social.entity.SocialId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialRepository extends JpaRepository<Social, SocialId> {
//    List<Social> findByMemberId(Long memberId);
//    void deleteByMemberIdAndFriendId(Long memberId, Long friendId);
}
