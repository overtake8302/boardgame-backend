package com.elice.boardgame.post.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PostVisitorId implements Serializable {

    @Column(name = "visitor_id")
    private String visitorId;

    @Column(name = "post_id")
    private Long postId;
}
