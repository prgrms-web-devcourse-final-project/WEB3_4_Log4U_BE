package com.example.log4u.common.util;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

public class PageableUtil {
	public static <T> Slice<T> checkAndCreateSlice(List<T> content, Pageable pageable) {
		boolean hasNext = content.size() > pageable.getPageSize();

		// 다음 페이지가 있으면 마지막 항목 제거
		if (hasNext) {
			content.remove(content.size() - 1);  // removeLast() 대신 인덱스로 처리
		}

		return new SliceImpl<>(content, pageable, hasNext);
	}
}
