package com.elice.boardgame.report.dto;

import java.util.List;
import lombok.Data;

@Data
public class ReportDto {
    private Long reportId;
    private String reporterName;
    private String reportedUserName;
    private Long reportedPostId;
    private String reportStatus;
    private String reportReason;
    private List<ReportAttachmentDto> attachments;

    public ReportDto(Long reportId, String reporterName, String reportedUserName, Long reportedPostId, String reportStatus, String reportReason, List<ReportAttachmentDto> attachments) {
        this.reportId = reportId;
        this.reporterName = reporterName;
        this.reportedUserName = reportedUserName;
        this.reportedPostId = reportedPostId;
        this.reportStatus = reportStatus;
        this.reportReason = reportReason;
        this.attachments = attachments;
    }
}
