package com.elice.boardgame.report.service;

import com.elice.boardgame.auth.entity.User;
import com.elice.boardgame.auth.repository.UserRepository;
import com.elice.boardgame.game.repository.BoardGameRepository;
import com.elice.boardgame.post.entity.Post;
import com.elice.boardgame.post.repository.PostRepository;
import com.elice.boardgame.report.dto.ReportAttachmentDto;
import com.elice.boardgame.report.dto.ReportCreateRequestDto;
import com.elice.boardgame.report.dto.ReportDto;
import com.elice.boardgame.report.dto.ReportUpdateRequestDto;
import com.elice.boardgame.report.entity.Report;
import com.elice.boardgame.report.repository.ReportRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReportService {

    private final ReportRepository reportRepository;

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    public List<ReportDto> findAll() {
        List<Report> reports = reportRepository.findAll();
        return reports.stream()
            .map(report -> new ReportDto(
                report.getReportId(),
                report.getReporter().getUsername(),
                report.getReportedUser() != null ? report.getReportedUser().getUsername() : null,
                report.getReportedPost() != null ? report.getReportedPost().getId() : null,
                report.getReportStatus(),
                report.getReportReason(),
                report.getAttachments().stream()
                    .map(attachment -> new ReportAttachmentDto(
                        attachment.getAttachmentId(),
                        attachment.getAttachmentUrl()))
                    .collect(Collectors.toList())))
            .collect(Collectors.toList());
    }

    @Transactional
    public void sendReport(ReportCreateRequestDto requestDto) {
        User reporter = userRepository.findById(requestDto.getReporterId()).orElseThrow();
        User repotredUser = userRepository.findById(requestDto.getReportedUserId()).orElseThrow();
        Post post = postRepository.getById(requestDto.getReportedPostId());
        Report report = new Report();
        report.setReporter(reporter);
        report.setReportedUser(repotredUser);
        report.setReportedPost(post);
        report.setReportReason(requestDto.getReportReason());
        report.setReportStatus("NEW");

        reportRepository.save(report);
    }

    @Transactional
    public void updateReport(ReportUpdateRequestDto requestDto) {
        Report report = reportRepository.findById(requestDto.getReportId())
            .orElseThrow(() -> new IllegalArgumentException(""));

        report.setReportStatus(requestDto.getReportStatus());

        reportRepository.save(report);
    }

}
