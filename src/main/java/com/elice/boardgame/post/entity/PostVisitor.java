package com.elice.boardgame.post.entity;

import com.elice.boardgame.common.entity.BaseEntity;
import com.elice.boardgame.game.entity.BoardGame;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "post_visitor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostVisitor extends BaseEntity {

    @EmbeddedId
    private PostVisitorId id;

    @MapsId("postId")
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    public PostVisitor(String visitorId, Long postId) {
        this.id = new PostVisitorId(visitorId, postId);
    }

}
