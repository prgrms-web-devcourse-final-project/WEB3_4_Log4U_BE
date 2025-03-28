package com.example.log4u.domain.supports.controller;

import com.example.log4u.domain.supports.dto.SupportCreateRequestDto;
import com.example.log4u.domain.supports.dto.SupportGetResponseDto;
import com.example.log4u.domain.supports.dto.SupportOverviewGetResponseDto;
import com.example.log4u.domain.supports.service.SupportService;
import com.example.log4u.domain.supports.supportType.SupportType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/supports")
public class SupportController {
    private final SupportService supportService;

    @PostMapping
    public ResponseEntity<Void> createSupport(
            @RequestBody @Valid SupportCreateRequestDto supportCreateRequestDto
            ){
        supportService.createSupport(supportCreateRequestDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<SupportOverviewGetResponseDto>> getSupportOverviewPage(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) SupportType supportType
    ){
        Page<SupportOverviewGetResponseDto> supportOverviewPage = supportService.getSupportPage(page, supportType);
        return ResponseEntity.ok().body(supportOverviewPage);
    }

    @GetMapping("/{supportId}")
    public ResponseEntity<SupportGetResponseDto> getSupportBySupportId(
            @PathVariable Long supportId){
        SupportGetResponseDto supportGetResponseDto = supportService.getSupportById(supportId);
        return ResponseEntity.ok().body(supportGetResponseDto);
    }
}
