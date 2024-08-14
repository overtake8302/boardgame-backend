package com.elice.boardgame.post.entity;

import com.elice.boardgame.auth.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "post_like")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostLike {

    @EmbeddedId
    private PostLikePK postLikePK;

    @ManyToOne
    @MapsId("postId")
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;
}
