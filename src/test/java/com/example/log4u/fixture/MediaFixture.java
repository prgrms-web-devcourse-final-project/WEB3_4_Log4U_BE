package com.example.log4u.fixture;

import com.example.log4u.domain.media.entity.Media;

public class MediaFixture {

	public static Media createMediaFixture(Long mediaId, Long diaryId) {
		return Media.builder()
			.mediaId(mediaId)
			.diaryId(diaryId)
			.originalName("image.jpg")
			.storedName("stored.jpg")
			.url("url.jpg")
			.contentType("image/jpeg")
			.size(1000L)
			.build();
	}
}
