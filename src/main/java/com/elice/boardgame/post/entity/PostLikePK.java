package com.elice.boardgame.post.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@EqualsAndHashCode
public class PostLikePK implements Serializable {
    private Long userId;

    private Long postId;

    public PostLikePK(Long postId) {
        this.postId = postId;
    }
}
