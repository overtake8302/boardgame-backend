package com.elice.boardgame.report.dto;

import java.util.List;
import lombok.Data;

@Data
public class ReportDto {
    private Long reportId;
    private Long reporterId;
    private Long reportedPostId;
    private Long reportedGameId;
    private String reportStatus;
    private String reportReason;
    private List<ReportAttachmentDto> attachments;

    public ReportDto(Long reportId, Long reporterId, Long reportedPostId, Long reportedGameId, String reportStatus, String reportReason, List<ReportAttachmentDto> attachments) {
        this.reportId = reportId;
        this.reporterId = reporterId;
        this.reportedPostId = reportedPostId;
        this.reportedGameId = reportedGameId;
        this.reportStatus = reportStatus;
        this.reportReason = reportReason;
        this.attachments = attachments;
    }
}
