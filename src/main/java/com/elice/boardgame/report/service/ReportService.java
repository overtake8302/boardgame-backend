package com.elice.boardgame.report.service;

import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.auth.repository.UserRepository;
import com.elice.boardgame.category.dto.PostPageDto;
import com.elice.boardgame.common.dto.PaginationRequest;
import com.elice.boardgame.common.exceptions.ReportNotFoundException;
import com.elice.boardgame.game.repository.BoardGameRepository;
import com.elice.boardgame.post.entity.Post;
import com.elice.boardgame.post.repository.PostRepository;
import com.elice.boardgame.report.dto.ReportAttachmentDto;
import com.elice.boardgame.report.dto.ReportCreateRequestDto;
import com.elice.boardgame.report.dto.ReportDto;
import com.elice.boardgame.report.dto.ReportUpdateRequestDto;
import com.elice.boardgame.report.entity.Report;
import com.elice.boardgame.report.entity.ReportAttachment;
import com.elice.boardgame.report.repository.ReportRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class ReportService {

    private final ReportRepository reportRepository;

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final ReportS3Service reportS3Service;

    public PostPageDto<ReportDto> find(String status, PaginationRequest paginationRequest) {
        PostPageDto<ReportDto> reportDtos;

        int page = paginationRequest.getPage();
        int size = paginationRequest.getSize();

        if ("완료".equals(status)) {
            reportDtos = findCompletedReports(page, size);
        } else {
            reportDtos = findWaitingReports(page, size);
        }

        return reportDtos;
    }


    public PostPageDto<ReportDto> findWaitingReports(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Report> reports = reportRepository.findWaitingReports(pageRequest);

        List<ReportDto> content = reports.getContent().stream().map(report -> new ReportDto(
            report.getReportId(),
            report.getReporter().getUsername(),
            report.getReportedUser() != null ? report.getReportedUser().getUsername() : null,
            report.getReportedPost() != null ? report.getReportedPost().getId() : null,
            report.getReportStatus(),
            report.getReportReason(),
            report.getAttachments().stream()
                .map(attachment -> new ReportAttachmentDto(attachment.getAttachmentUrl()))
                .collect(Collectors.toList())
        )).collect(Collectors.toList());

        return PostPageDto.<ReportDto>builder().content(content).pageNumber(page).pageSize(size).totalElements(
            reports.getTotalElements()).totalPages(reports.getTotalPages()).build();
    }

    public PostPageDto<ReportDto> findCompletedReports(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Report> reports = reportRepository.findCompletedReports(pageRequest);

        List<ReportDto> content = reports.getContent().stream().map(report -> new ReportDto(
            report.getReportId(),
            report.getReporter().getUsername(),
            report.getReportedUser() != null ? report.getReportedUser().getUsername() : null,
            report.getReportedPost() != null ? report.getReportedPost().getId() : null,
            report.getReportStatus(),
            report.getReportReason(),
            report.getAttachments().stream()
                .map(attachment -> new ReportAttachmentDto(attachment.getAttachmentUrl()))
                .collect(Collectors.toList())
        )).collect(Collectors.toList());

        return PostPageDto.<ReportDto>builder().content(content).pageNumber(page).pageSize(size).totalElements(
            reports.getTotalElements()).totalPages(reports.getTotalPages()).build();
    }

    @Transactional
    public void sendReport(ReportCreateRequestDto requestDto, List<MultipartFile> attachments) {
        Report report = new Report();
        report.setAttachments(new ArrayList<>());

        User reporter = userRepository.findById(requestDto.getReporterId()).orElseThrow();
        report.setReporter(reporter);

        User reportedUser = requestDto.getReportedUserId() != null
            ? userRepository.findById(requestDto.getReportedUserId()).orElse(null)
            : null;
        report.setReportedUser(reportedUser);

        Post post = requestDto.getReportedPostId() != null
            ? postRepository.findById(requestDto.getReportedPostId()).orElse(null)
            : null;
        report.setReportedPost(post);

        if (reportedUser == null && post == null) {
            throw new ReportNotFoundException("reportedUser와 post 중 하나는 반드시 존재해야 합니다.");
        }

        report.setReportReason(requestDto.getReportReason());
        report.setReportStatus("진행 전");

        attachments.forEach(file -> {
            ReportAttachment attachment = new ReportAttachment();
            attachment.setReport(report);
            attachment.setAttachmentUrl(reportS3Service.uploadFile(file));
            report.getAttachments().add(attachment);
        });

        reportRepository.save(report);
    }

    @Transactional
    public void updateReport(ReportUpdateRequestDto requestDto) {
        Report report = reportRepository.findById(requestDto.getReportId())
            .orElseThrow(() -> new ReportNotFoundException("해당 신고를 찾을 수 없습니다."));

        report.setReportStatus(requestDto.getReportStatus());

        reportRepository.save(report);
    }

}
