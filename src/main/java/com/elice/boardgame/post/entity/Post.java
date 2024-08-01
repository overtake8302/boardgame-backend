package com.elice.boardgame.post.entity;

import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.enums.Enums;
import com.elice.boardgame.game.entity.BoardGame;

import java.util.ArrayList;
import java.util.List;

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
@Entity
@SQLDelete(sql = "UPDATE orders SET deletedAt = true WHERE deletedAt = ?")
@Where(clause = "deletedAt = false")
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @ManyToOne
    @JoinColumn(name = "gameId", nullable = false)
    private BoardGame boardGame;

    private String category;
    private String title;
    private String content;
    private String imageUrl;
    private String imageName;
    private String gameName;

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private View view;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public Long getUserId() {
        return user != null ? user.getId() : null;
    }
}
