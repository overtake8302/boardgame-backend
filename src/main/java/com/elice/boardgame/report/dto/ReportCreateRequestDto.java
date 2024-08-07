package com.elice.boardgame.report.dto;

import java.util.List;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ReportCreateRequestDto {
    private Long reporterId;
    private Long reportedUserId;
    private Long reportedPostId;
    private String reportReason;
    private List<MultipartFile> attachments;
}
