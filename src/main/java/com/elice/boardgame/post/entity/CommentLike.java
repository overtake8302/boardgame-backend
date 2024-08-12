package com.elice.boardgame.post.entity;

import com.elice.boardgame.auth.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity (name = "comment_like")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentLike {
    @EmbeddedId
    private CommentLikePK commentLikePK;

    @ManyToOne
    @MapsId("commentId")
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    public CommentLike(CommentLikePK commentLikePK, Comment comment) {
        this.commentLikePK = commentLikePK;
        this.comment = comment;
    }
}
