package com.example.log4u.domain.supports.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.log4u.domain.supports.dto.SupportCreateRequestDto;
import com.example.log4u.domain.supports.dto.SupportGetResponseDto;
import com.example.log4u.domain.supports.dto.SupportOverviewGetResponseDto;
import com.example.log4u.domain.supports.entity.Support;
import com.example.log4u.domain.supports.repository.SupportQuerydsl;
import com.example.log4u.domain.supports.repository.SupportRepository;
import com.example.log4u.domain.supports.supportType.SupportType;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class SupportService {
	private final SupportRepository supportRepository;
	private final SupportQuerydsl supportQuerydsl;

	public void createSupport(
		long requesterId,
		SupportCreateRequestDto supportCreateRequestDto) {
		Support support = supportCreateRequestDto.toEntity(requesterId);
		supportRepository.save(support);
	}

	@Transactional(readOnly = true)
	public Page<SupportOverviewGetResponseDto> getSupportPage(
		long requesterId,
		int page,
		SupportType supportType
	) {
		Pageable pageable = PageRequest.of(page - 1, 10);
		return supportQuerydsl.getSupportOverviewGetResponseDtoPage(requesterId, pageable, supportType);
	}

	@Transactional(readOnly = true)
	public SupportGetResponseDto getSupportById(
		long requesterId,
		Long supportId) {
		return supportQuerydsl.getSupportGetResponseDtoById(requesterId, supportId);
	}
}
