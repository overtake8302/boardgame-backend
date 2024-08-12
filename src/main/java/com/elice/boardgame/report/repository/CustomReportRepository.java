package com.elice.boardgame.report.repository;

import com.elice.boardgame.report.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomReportRepository {
    Page<Report> findWaitingReports(Pageable pageable);
    Page<Report> findCompletedReports(Pageable pageable);
}
