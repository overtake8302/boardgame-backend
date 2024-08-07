package com.elice.boardgame.post.entity;

import com.elice.boardgame.auth.entity.User;
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
@Entity
@SQLDelete(sql = "UPDATE post SET deletedAt = true WHERE post_id = ?")
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

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    private String category;
    private String title;
    private List<String> imageUrls = new ArrayList<>();
    private List<String> imageNames = new ArrayList<>();
    private String gameName;
    private List<String> gameImageUrls = new ArrayList<>();

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private View view;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonManagedReference
    private List<Comment> comments = new ArrayList<>();

    public Long getUserId() {
        return user != null ? user.getId() : null;
    }

    public void addImageUrl(String imageUrl) {
        this.imageUrls.add(imageUrl);
    }

    public void addImageName(String imageName) {
        this.imageNames.add(imageName);
    }
}
