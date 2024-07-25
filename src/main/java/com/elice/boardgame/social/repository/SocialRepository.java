package com.elice.boardgame.social.repository;

import com.elice.boardgame.social.entity.Social;
import com.elice.boardgame.social.entity.SocialId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialRepository extends JpaRepository<Social, SocialId> {
    // 특정 회원의 친구 목록 조회
    List<Social> findByMemberId(Long memberId);

    // 친구 삭제: 회원과 친구의 관계 삭제
    void deleteByMemberIdAndFriendId(Long memberId, Long friendId);
}
