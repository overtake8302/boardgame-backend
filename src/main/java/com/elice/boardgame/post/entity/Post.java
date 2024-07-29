package com.elice.boardgame.post.entity;

import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.game.entity.BoardGame;

import com.elice.boardgame.game.entity.BoardGame.PlayTime;
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
@SQLDelete(sql = "UPDATE post SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private BoardGame boardGame;

    @Enumerated(EnumType.STRING)
    private Category category;

    private String title;
    private String content;
    private String imageUrl;
    private String imageName;

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private View view;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)  //  cascade = CascadeType.???? <- 이게 대댓글이 삭제되는게 아닌 해당하는 댓글만 삭제되는건지는 모르겠음 일단 좀더 알아보자
    private List<Comment> comments = new ArrayList<>();

    public Long getUserId() {
        return user.getId();
    }

    public Long getGameId() {
        return boardGame.getGameId();
    }

    @AllArgsConstructor
    @Getter
    public enum Category {
        FREE("자유 게시판"),
        USED("중고 거래 게시판"),
        MEETING("모임 게시판");

        private final String label;
    }
}
