package com.elice.boardgame.report.controller;

import com.elice.boardgame.report.dto.ReportDto;
import com.elice.boardgame.report.service.ReportService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;

    @GetMapping()
    public List<ReportDto> findAll() {
        return reportService.findAll();
    }

    @PutMapping("/send")
    public void sendReport() {

    }

    @PutMapping("/update")
    public void updateReport() {

    }
}
