package com.elice.boardgame.report.controller;

import com.elice.boardgame.report.dto.ReportDto;
import com.elice.boardgame.report.service.ReportService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping()
    public List<ReportDto> findAll() {
        return reportService.findAll();
    }

    @PutMapping()
    public void sendReport() {

    }

    @PutMapping()
    public void updateReport() {

    }
}
