package com.elice.boardgame.report.controller;

import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.category.dto.PostPageDto;
import com.elice.boardgame.common.annotation.CurrentUser;
import com.elice.boardgame.common.dto.CommonResponse;
import com.elice.boardgame.common.dto.PaginationRequest;
import com.elice.boardgame.report.dto.ReportCreateRequestDto;
import com.elice.boardgame.report.dto.ReportDto;
import com.elice.boardgame.report.dto.ReportUpdateRequestDto;
import com.elice.boardgame.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "리포트 조회", description = "상태에 따른 리포트를 조회합니다.", parameters = {
        @Parameter(name = "status", description = "리포트 상태", required = true, schema = @Schema(type = "String", example = "PENDING")),
        @Parameter(name = "paginationRequest", description = "페이지네이션 요청", required = true, schema = @Schema(implementation = PaginationRequest.class))
    })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공적으로 조회됨"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @GetMapping
    public CommonResponse<PostPageDto<ReportDto>> findReports(@RequestParam String status,
        PaginationRequest paginationRequest) {

        System.out.println(paginationRequest.getPage() + paginationRequest.getSize());
        PostPageDto<ReportDto> reportDtos = reportService.find(status, paginationRequest);

        return CommonResponse.<PostPageDto<ReportDto>>builder()
            .payload(reportDtos)
            .message("")
            .status(200)
            .build();
    }

    @Operation(summary = "리포트 전송", description = "새로운 리포트를 전송합니다.", parameters = {
        @Parameter(name = "reportData", description = "리포트 생성 요청 데이터", required = true, schema = @Schema(implementation = ReportCreateRequestDto.class)),
        @Parameter(name = "attachments", description = "첨부 파일 목록", schema = @Schema(type = "List", implementation = MultipartFile.class)),
        @Parameter(name = "user", description = "현재 사용자", required = true, schema = @Schema(implementation = User.class))
    })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공적으로 전송됨"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/send")
    public void sendReport(
        @RequestPart("reportData") ReportCreateRequestDto requestDto,
        @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments,
        @CurrentUser User user) {
        reportService.sendReport(requestDto, attachments, user);
    }

    @Operation(summary = "리포트 수정", description = "기존 리포트를 수정합니다.", parameters = {
        @Parameter(name = "reportUpdateRequest", description = "리포트 수정 요청 데이터", required = true, schema = @Schema(implementation = ReportUpdateRequestDto.class))
    })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공적으로 수정됨"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PutMapping
    public void updateReport(@RequestBody ReportUpdateRequestDto requestDto) {
        reportService.updateReport(requestDto);
    }
}
