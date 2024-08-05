package com.elice.boardgame.report.entity;

import com.elice.boardgame.post.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ReportAttachment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attachmentId;

    @ManyToOne
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    private String attachmentUrl;
}
