package com.elice.boardgame.social.entity;

import com.elice.boardgame.auth.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "social")
@Getter
@Setter
@NoArgsConstructor
public class Social {
    @EmbeddedId
    private SocialId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("friendId")
    @JoinColumn(name = "friend_id")
    private User friend;

    public Social(SocialId id) {
        this.id = id;
    }
}
