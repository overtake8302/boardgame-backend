package com.elice.boardgame.post.entity;

import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.common.entity.BaseEntity;
import com.elice.boardgame.game.entity.BoardGame;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "post")
@SQLDelete(sql = "UPDATE post SET deleted_at = CURRENT_TIMESTAMP WHERE post_id = ?")
@Where(clause = "deleted_at IS NULL")
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private BoardGame boardGame;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String content;

    private String category;
    private String title;

    @Column(name = "game_name")
    private String gameName;

    @Column(name = "game_image_url")
    private String gameImageUrl;

    @Column(name = "like_count")
    private Long likeCount;

    /*@OneToOne(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private View view;*/

    private Long view;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("post")
    private List<Comment> comments = new ArrayList<>();

    public Long getUserId() {
        return user != null ? user.getId() : null;
    }
}
