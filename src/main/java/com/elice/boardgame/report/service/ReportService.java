package com.elice.boardgame.report.service;

import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.auth.repository.UserRepository;
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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class ReportService {

    private final ReportRepository reportRepository;

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final ReportS3Service reportS3Service;

    public List<ReportDto> findAll() {
        List<Report> reports = reportRepository.findAll();
        return reports.stream().map(report -> {
            String reporterName = report.getReporter().getUsername();
            String reportedUserName = report.getReportedUser() != null ? report.getReportedUser().getUsername() : null;
            Long reportedPostId = report.getReportedPost() != null ? report.getReportedPost().getId() : null;
            List<ReportAttachmentDto> attachments = report.getAttachments().stream()
                .map(attachment -> new ReportAttachmentDto(attachment.getAttachmentUrl()))
                .collect(Collectors.toList());
            return new ReportDto(
                report.getReportId(),
                reporterName,
                reportedUserName,
                reportedPostId,
                report.getReportStatus(),
                report.getReportReason(),
                attachments
            );
        }).collect(Collectors.toList());
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
        report.setReportStatus("NEW");

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
