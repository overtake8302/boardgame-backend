package com.elice.boardgame.social.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class SocialId implements Serializable {
    private Long userId;
    private Long friendId;

    public SocialId() {}

    public SocialId(Long userId, Long friendId) {
        this.userId = userId;
        this.friendId = friendId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SocialId socialId = (SocialId) o;
        return Objects.equals(userId, socialId.userId) && Objects.equals(friendId, socialId.friendId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, friendId);
    }

}
