package com.example.log4u.domain.supports.service;

import com.example.log4u.domain.supports.dto.SupportCreateRequestDto;
import com.example.log4u.domain.supports.dto.SupportGetResponseDto;
import com.example.log4u.domain.supports.dto.SupportOverviewGetResponseDto;
import com.example.log4u.domain.supports.entity.Support;
import com.example.log4u.domain.supports.repository.SupportQuerydsl;
import com.example.log4u.domain.supports.repository.SupportRepository;
import com.example.log4u.domain.supports.supportType.SupportType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SupportService {
    private final SupportRepository supportRepository;
    private final SupportQuerydsl supportQuerydsl;

    public void createSupport(SupportCreateRequestDto supportCreateRequestDto) {
        Support support = supportCreateRequestDto.toEntity();
        supportRepository.save(support);
    }

    @Transactional(readOnly = true)
    public Page<SupportOverviewGetResponseDto> getSupportPage(
            Integer page,
            SupportType supportType
    ){
        int primitivePage = page == null ? 0 : page - 1;
        Pageable pageable = PageRequest.of(primitivePage, 10);
        return supportQuerydsl.getSupportOverviewGetResponseDtoPage(pageable, supportType);
    }

    @Transactional(readOnly = true)
    public SupportGetResponseDto getSupportById(Long supportId) {
        return supportQuerydsl.getSupportGetResponseDtoById(supportId);
    }
}
