package com.example.log4u.fixture;

import java.util.ArrayList;
import java.util.List;

import com.example.log4u.domain.hashtag.entity.Hashtag;

public class HashtagFixture {

	// 기본 해시태그 생성
	public static Hashtag createDefaultHashtag() {
		return Hashtag.builder()
			.hashtagId(1L)
			.name("여행")
			.build();
	}

	// ID와 이름으로 해시태그 생성
	public static Hashtag createHashtag(Long hashtagId, String name) {
		return Hashtag.builder()
			.hashtagId(hashtagId)
			.name(name)
			.build();
	}

	// 자주 사용하는 해시태그 상수
	public static final Hashtag TRAVEL = createHashtag(1L, "여행");
	public static final Hashtag FOOD = createHashtag(2L, "맛집");
	public static final Hashtag DAILY = createHashtag(3L, "일상");
	public static final Hashtag CAFE = createHashtag(4L, "카페");
	public static final Hashtag PHOTO = createHashtag(5L, "사진");
	public static final Hashtag JEJU = createHashtag(6L, "제주도");
	public static final Hashtag SEOUL = createHashtag(7L, "서울");
	public static final Hashtag FRIEND = createHashtag(8L, "친구");
	public static final Hashtag MOVIE = createHashtag(9L, "영화");
	public static final Hashtag MUSIC = createHashtag(10L, "음악");

	// 여러 해시태그 생성
	public static List<Hashtag> createHashtags() {
		List<Hashtag> hashtags = new ArrayList<>();
		hashtags.add(TRAVEL);
		hashtags.add(FOOD);
		hashtags.add(DAILY);
		hashtags.add(CAFE);
		hashtags.add(PHOTO);
		return hashtags;
	}

	// 특정 이름 목록으로 해시태그 생성
	public static List<Hashtag> createHashtagsByNames(List<String> names) {
		List<Hashtag> hashtags = new ArrayList<>();
		for (int i = 0; i < names.size(); i++) {
			hashtags.add(createHashtag((long)(i + 1), names.get(i)));
		}
		return hashtags;
	}

	// 특정 개수만큼 해시태그 생성
	public static List<Hashtag> createHashtags(int count) {
		List<Hashtag> hashtags = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			hashtags.add(createHashtag((long)(i + 1), "해시태그" + (i + 1)));
		}
		return hashtags;
	}

	// 해시태그 이름 목록 생성 (# 포함)
	public static List<String> createHashtagNames(String... names) {
		List<String> hashtagNames = new ArrayList<>();
		for (String name : names) {
			hashtagNames.add("#" + name);
		}
		return hashtagNames;
	}

	// 해시태그 이름 목록 생성 (# 미포함)
	public static List<String> createRawHashtagNames(String... names) {
		return List.of(names);
	}
}