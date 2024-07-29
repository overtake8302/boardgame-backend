package com.elice.boardgame.social.service;

import com.elice.boardgame.social.dto.SocialRequest;
import com.elice.boardgame.social.entity.Social;
import com.elice.boardgame.social.entity.SocialId;
import com.elice.boardgame.social.exception.SocialAlreadyExistsException;
import com.elice.boardgame.social.exception.SocialNotFoundException;
import com.elice.boardgame.social.repository.SocialRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class SocialService {
    private final SocialRepository socialRepository;

    public List<Long> getFriendIds(Long userId) {
        return socialRepository.findFriendIdsByUserId(userId);
    }

    public void addFriend(SocialRequest socialRequest) {
        Long userId = socialRequest.getUserId();
        Long friendId = socialRequest.getFriendId();

        if (socialRepository.existsByUserIdAndFriendId(userId, friendId)) {
            throw new SocialAlreadyExistsException("Friend relationship already exists.");
        }

        SocialId socialId = new SocialId(userId, friendId);
        Social social = new Social();
        social.setId(socialId);

        socialRepository.save(social);
    }

    public void removeFriend(Long userId, Long friendId) {
        if (!socialRepository.existsByUserIdAndFriendId(userId, friendId)) {
            throw new SocialNotFoundException("Social not found.");
        }
        socialRepository.deleteByUserIdAndFriendId(userId, friendId);
    }
}
