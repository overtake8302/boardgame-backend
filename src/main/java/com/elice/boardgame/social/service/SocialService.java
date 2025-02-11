package com.elice.boardgame.social.service;

import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.auth.repository.UserRepository;
import com.elice.boardgame.common.exceptions.MemberNotFoundException;
import com.elice.boardgame.social.dto.SocialResponse;
import com.elice.boardgame.social.entity.Social;
import com.elice.boardgame.social.entity.SocialId;
import com.elice.boardgame.social.exception.SocialAlreadyExistsException;
import com.elice.boardgame.social.exception.SocialNotFoundException;
import com.elice.boardgame.social.repository.SocialRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.model.ResourceNotFoundException;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class SocialService {
    private final SocialRepository socialRepository;
    private final UserRepository userRepository;

    public Page<SocialResponse> getFriendIds(Long userId, Pageable pageable) {
        List<Long> friendIds = socialRepository.findFriendIdsByUserId(userId);
        List<User> friends = userRepository.findAllById(friendIds);

        List<SocialResponse> friendResponses = friends.stream()
            .map(user -> new SocialResponse(user.getId(), user.getUsername()))
            .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), friendResponses.size());
        List<SocialResponse> pagedFriendResponses = friendResponses.subList(start, end);

        return new PageImpl<>(pagedFriendResponses, pageable, friendResponses.size());
    }


    public void addFriend(Long userId, Long friendId) {
        if (socialRepository.existsByUserIdAndFriendId(userId, friendId)) {
            throw new SocialAlreadyExistsException("Friend relationship already exists.");
        }

        // User와 Friend 엔티티를 각각 조회하여 설정
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new MemberNotFoundException("User not found with id: " + userId));
        User friend = userRepository.findById(friendId)
            .orElseThrow(() -> new MemberNotFoundException("Friend not found with id: " + friendId));

        SocialId socialId = new SocialId(userId, friendId);
        Social social = new Social();
        social.setId(socialId);

        // 엔티티 연관 관계 설정
        social.setUser(user);
        social.setFriend(friend);

        socialRepository.save(social);
    }


    public void removeFriend(Long userId, Long friendId) {
        if (!socialRepository.existsByUserIdAndFriendId(userId, friendId)) {
            throw new SocialNotFoundException("Social relationship not found.");
        }
        socialRepository.deleteByUserIdAndFriendIdBothWays(userId, friendId);
    }
}
