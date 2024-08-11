package com.elice.boardgame.report.dto;

import lombok.Data;

@Data
public class ReportUpdateRequestDto {
    private Long reportId;
    private String reportStatus;
}
