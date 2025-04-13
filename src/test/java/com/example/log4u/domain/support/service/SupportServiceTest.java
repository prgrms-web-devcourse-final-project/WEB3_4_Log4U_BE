package com.example.log4u.domain.support.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.example.log4u.common.dto.PageResponse;
import com.example.log4u.domain.supports.dto.SupportCreateRequestDto;
import com.example.log4u.domain.supports.dto.SupportGetResponseDto;
import com.example.log4u.domain.supports.dto.SupportOverviewGetResponseDto;
import com.example.log4u.domain.supports.entity.Support;
import com.example.log4u.domain.supports.repository.SupportQuerydsl;
import com.example.log4u.domain.supports.repository.SupportRepository;
import com.example.log4u.domain.supports.service.SupportService;
import com.example.log4u.domain.supports.supportType.SupportType;

@DisplayName("문의 API 단위 테스트")
@ExtendWith(MockitoExtension.class)
public class SupportServiceTest {
	@InjectMocks
	private SupportService supportService;

	@Mock
	private SupportRepository supportRepository;

	@Mock
	private SupportQuerydsl supportQuerydsl;

	@DisplayName("성공 테스트 : 문의 등록")
	@Test
	void testCreateSupport() {
		long requesterId = 1L;
		SupportCreateRequestDto supportCreateRequestDto = mock(SupportCreateRequestDto.class);
		Support supportEntity = mock(Support.class);

		given(supportCreateRequestDto.toEntity(requesterId)).willReturn(supportEntity);

		supportService.createSupport(requesterId, supportCreateRequestDto);

		verify(supportRepository).save(supportEntity);
	}

	@DisplayName("성공 테스트 : 문의 페이지 조회")
	@Test
	void testGetSupportPage() {
		long requesterId = 1L;
		SupportType supportType = SupportType.ETC;
		PageRequest pageable = PageRequest.of(0, 10);
		SupportOverviewGetResponseDto supportOverview = mock(SupportOverviewGetResponseDto.class);
		Page<SupportOverviewGetResponseDto> supportPage = new PageImpl<>(List.of(supportOverview));

		given(supportQuerydsl.getSupportOverviewGetResponseDtoPage(requesterId, pageable, supportType))
			.willReturn(supportPage);

		PageResponse<SupportOverviewGetResponseDto> result = supportService.getSupportPage(requesterId, 1, supportType);

		assertNotNull(result);
		assertEquals(1, result.pageInfo().totalElements());
	}

	@DisplayName("성공 테스트 : 특정 문의 상세 조회")
	@Test
	void testGetSupportById() {
		long requesterId = 1L;
		Long supportId = 1L;
		SupportGetResponseDto supportGetResponseDto = mock(SupportGetResponseDto.class);

		given(supportQuerydsl.getSupportGetResponseDtoById(requesterId, supportId))
			.willReturn(supportGetResponseDto);

		SupportGetResponseDto result = supportService.getSupportById(requesterId, supportId);

		assertNotNull(result);
		verify(supportQuerydsl).getSupportGetResponseDtoById(requesterId, supportId);
	}
}
