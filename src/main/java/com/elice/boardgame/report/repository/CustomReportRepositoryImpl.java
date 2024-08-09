package com.elice.boardgame.report.repository;

import com.elice.boardgame.report.entity.QReport;
import com.elice.boardgame.report.entity.Report;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

public class CustomReportRepositoryImpl implements CustomReportRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Report> findWaitingReports(Pageable pageable) {
        QReport report = QReport.report;
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        JPQLQuery<Report> query = queryFactory.selectFrom(report)
            .where(report.reportStatus.in("진행 전", "진행 중"))
            .orderBy(report.createdAt.desc());

        return PageableExecutionUtils.getPage(query.fetch(), pageable, query::fetchCount);
    }

    @Override
    public Page<Report> findCompletedReports(Pageable pageable) {
        QReport report = QReport.report;
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        JPQLQuery<Report> query = queryFactory.selectFrom(report)
            .where(report.reportStatus.eq("완료"))
            .orderBy(report.createdAt.desc());

        return PageableExecutionUtils.getPage(query.fetch(), pageable, query::fetchCount);
    }
}
