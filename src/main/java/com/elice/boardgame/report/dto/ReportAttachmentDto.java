package com.elice.boardgame.report.dto;

import lombok.Data;

@Data
public class ReportAttachmentDto {
    private Long attachmentId;
    private String attachmentUrl;

    public ReportAttachmentDto(Long attachmentId, String attachmentUrl) {
        this.attachmentId = attachmentId;
        this.attachmentUrl = attachmentUrl;
    }
}
