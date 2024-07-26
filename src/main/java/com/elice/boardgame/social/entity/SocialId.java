package com.elice.boardgame.social.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class SocialId implements Serializable {
    private Long memberId;
    private Long friendId;

    public SocialId() {}

    public SocialId(Long memberId, Long friendId) {
        this.memberId = memberId;
        this.friendId = friendId;
    }

    // equals and hashCode...

}
