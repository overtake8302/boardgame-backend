package com.elice.boardgame.social.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Social {
    @EmbeddedId
    private SocialId id;

//    @ManyToOne
//    @MapsId("memberId")
//    @JoinColumn(name = "member_id")
//    private Member member;
//
//    @ManyToOne
//    @MapsId("friendId")
//    @JoinColumn(name = "friend_id")
//    private Member friend;

}
