package com.elice.boardgame.report.controller;

import com.elice.boardgame.category.dto.PostPageDto;
import com.elice.boardgame.common.dto.CommonResponse;
import com.elice.boardgame.common.dto.PaginationRequest;
import com.elice.boardgame.report.dto.ReportCreateRequestDto;
import com.elice.boardgame.report.dto.ReportDto;
import com.elice.boardgame.report.dto.ReportUpdateRequestDto;
import com.elice.boardgame.report.service.ReportService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    public CommonResponse<PostPageDto<ReportDto>> findReports(@RequestParam String status, @RequestParam
        PaginationRequest paginationRequest) {
        PostPageDto<ReportDto> reportDtos = reportService.find(status, paginationRequest);

        return CommonResponse.<PostPageDto<ReportDto>>builder()
            .payload(reportDtos)
            .message("")
            .status(200)
            .build();
    }

    @PostMapping("/send")
    public void sendReport(
        @RequestPart("reportData") ReportCreateRequestDto requestDto,
        @RequestPart("attachments") List<MultipartFile> attachments) {
        reportService.sendReport(requestDto, attachments);
    }

    @PutMapping
    public void updateReport(@RequestBody ReportUpdateRequestDto requestDto) {
        reportService.updateReport(requestDto);
    }
}
