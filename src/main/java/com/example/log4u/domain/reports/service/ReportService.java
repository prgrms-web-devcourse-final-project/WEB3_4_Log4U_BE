package com.example.log4u.domain.reports.service;

import com.example.log4u.domain.reports.dto.ReportCreateRequestDto;
import com.example.log4u.domain.reports.entity.Report;
import com.example.log4u.domain.reports.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;

    //diary 엔티티 생긴 후 수정
    public void createDiaryReport(
            ReportCreateRequestDto reportCreateRequestDto,
            Long diaryId) {
        Report report = reportCreateRequestDto.toEntity(Report.ReportTargetType.DIARY, diaryId);
        reportRepository.save(report);
    }

    //comment 엔티티 생긴 후 수정
    public void createCommentReport(
            ReportCreateRequestDto reportCreateRequestDto,
            Long commentId) {
        Report report = reportCreateRequestDto.toEntity(Report.ReportTargetType.DIARY, commentId);
        reportRepository.save(report);
    }
}
