package com.example.log4u.domain.reports.controller;

import com.example.log4u.domain.reports.dto.ReportCreateRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reports")
public class ReportsController {

    @PostMapping("/diaries/{diaryId}")
    public ResponseEntity<Void> createReportForDiary(
            @RequestBody ReportCreateRequestDto reportCreateRequestDto,
            @PathVariable Long diaryId) {
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/comments/{commentId}")
    public ResponseEntity<Void> createReport(
            @RequestBody ReportCreateRequestDto reportCreateRequestDto,
            @PathVariable Long commentId) {
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
