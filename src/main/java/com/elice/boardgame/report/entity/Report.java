package com.elice.boardgame.report.entity;

import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.game.entity.BaseEntity;
import com.elice.boardgame.post.entity.Post;
import jakarta.persistence.*;
import java.util.List;
import lombok.Data;


@Entity
@Data
public class Report extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @ManyToOne
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @ManyToOne
    @JoinColumn(name = "reported_user_id")
    private User reportedUser;

    @ManyToOne
    @JoinColumn(name = "reported_post_id")
    private Post reportedPost;

    @Column(nullable = false)
    private String reportStatus;

    @Column(nullable = false)
    private String reportReason;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReportAttachment> attachments;

}

