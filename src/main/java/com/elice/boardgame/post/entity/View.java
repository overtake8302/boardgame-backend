package com.elice.boardgame.post.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "view")
public class View {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "view_count")
    private int viewCount;

   /* @ManyToOne
    @JoinColumn(name = "post_id", nullable = false, insertable = true, updatable = true)
    private Post post;

    public View(Post post, int viewCount) {
        this.post = post;
        this.viewCount = viewCount;
    }*/
}