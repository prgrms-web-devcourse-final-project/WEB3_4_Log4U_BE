package com.example.log4u.common.dto;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

public record PageResponse<T>(
	List<T> list,
	PageInfo pageInfo
) {
	// 오프셋 기반 (검색용)
	public static <T> PageResponse<T> of(Page<T> page) {
		return new PageResponse<>(
			page.getContent(),
			PageInfo.of(page)
		);
	}

	// 커서 기반 (무한 스크롤용)
	public static <T> PageResponse<T> of(Slice<T> slice, Long nextCursor) {
		return new PageResponse<>(
			slice.getContent(),
			PageInfo.of(slice, nextCursor)
		);
	}

	public record PageInfo(
		Integer page,
		int size,
		Long totalElements,
		Integer totalPages,
		boolean hasNext,
		Long nextCursor
	) {
		// Page용 팩토리 메서드
		public static PageInfo of(Page<?> page) {
			return new PageInfo(
				page.getNumber(),
				page.getSize(),
				page.getTotalElements(),
				page.getTotalPages(),
				page.hasNext(),
				null
			);
		}

		// Slice용 팩토리 메서드
		public static PageInfo of(Slice<?> slice, Long nextCursor) {
			return new PageInfo(
				null,
				slice.getSize(),
				null,
				null,
				slice.hasNext(),
				nextCursor
			);
		}
	}
}