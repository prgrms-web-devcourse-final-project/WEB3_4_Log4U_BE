package com.example.log4u.reports.dto;

public record ReportCreateRequestDto(

) {
    public enum ReportType {
        INAPPROPRIATE_CONTENT,
        FALSE_INFORMATION,
        SPAM,
        COPYRIGHT_INFRINGEMENT,
        PRIVACY_VIOLATION,
        ETC
    }
}
