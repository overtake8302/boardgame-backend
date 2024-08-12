package com.elice.boardgame.report.repository;

import com.elice.boardgame.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long>, CustomReportRepository { }
