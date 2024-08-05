package com.elice.boardgame.report.dto;

import lombok.Data;

@Data
public class ReportCreateRequestDto {
    private Long reporterId;
    private Long reportedUserId;
    private Long reportedPostId;
    private String reportReason;
}
