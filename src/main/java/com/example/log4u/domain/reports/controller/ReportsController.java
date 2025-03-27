package com.example.log4u.domain.reports.controller;

import com.example.log4u.domain.reports.dto.ReportCreateRequestDto;
import com.example.log4u.domain.reports.entity.Report;
import com.example.log4u.domain.reports.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportsController {
    private final ReportService reportService;

    @PostMapping("/diaries/{diaryId}")
    public ResponseEntity<Void> createReportForDiary(
            @RequestBody ReportCreateRequestDto reportCreateRequestDto,
            @PathVariable Long diaryId) {
        reportService.createDiaryReport(reportCreateRequestDto, diaryId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/comments/{commentId}")
    public ResponseEntity<Void> createReport(
            @RequestBody ReportCreateRequestDto reportCreateRequestDto,
            @PathVariable Long commentId) {
        reportService.createCommentReport(reportCreateRequestDto, commentId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
