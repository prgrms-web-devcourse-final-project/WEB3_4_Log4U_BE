package com.example.log4u.domain.supports.controller;

import com.example.log4u.domain.supports.dto.SupportCreateRequestDto;
import com.example.log4u.domain.supports.dto.SupportGetResponseDto;
import com.example.log4u.domain.supports.dto.SupportOverviewGetResponseDto;
import com.example.log4u.domain.supports.supportType.SupportType;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/supports")
public class SupportController {

    @PostMapping
    public ResponseEntity<Void> createSupport(
            @RequestBody @Valid SupportCreateRequestDto supportCreateRequestDto
            ){
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<SupportOverviewGetResponseDto>> getSupportOverviewPage(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) SupportType supportType
    ){
        List<SupportOverviewGetResponseDto> content = new ArrayList<>();

        content.add(SupportOverviewGetResponseDto.builder()
                .id(10L)
                .supportType(SupportType.PAYMENT_ISSUE)
                .title("결제 문의 10")
                .createdAt(LocalDateTime.of(2025,3,20, 12, 0))
                .answered(false)
                .build());

        content.add(SupportOverviewGetResponseDto.builder()
                .id(9L)
                .supportType(SupportType.PAYMENT_ISSUE)
                .title("결제 문의 9")
                .createdAt(LocalDateTime.of(2025,3,20, 12, 0))
                .answered(false)
                .build());

        Pageable pageable = PageRequest.of(0, 2);

        return ResponseEntity.ok().body(
                new PageImpl<>(content, pageable, 10));
    }

    @GetMapping("/{supportId}")
    public ResponseEntity<SupportGetResponseDto> getSupportBySupportId(
            @PathVariable Long supportId){
        SupportGetResponseDto supportGetResponseDto = SupportGetResponseDto.builder()
                .id(50L)
                .supportType(SupportType.PAYMENT_ISSUE)
                .title("결제 문의 50")
                .content("결제 문의 내용 50")
                .createdAt(LocalDateTime.of(2025,3,20,12,0))
                .answerContent("문의 답변 내용")
                .answeredAt(LocalDateTime.of(2025,3,20,12,0))
                .build();
        return ResponseEntity.ok().body(supportGetResponseDto);
    }
}
