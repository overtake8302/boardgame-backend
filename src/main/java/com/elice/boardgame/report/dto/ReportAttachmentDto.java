package com.elice.boardgame.report.dto;

import lombok.Data;

@Data
public class ReportAttachmentDto {
    private String attachmentUrl;

    public ReportAttachmentDto(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }
}
