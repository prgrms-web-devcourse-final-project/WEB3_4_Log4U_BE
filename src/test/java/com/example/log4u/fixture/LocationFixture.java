package com.example.log4u.fixture;

import com.example.log4u.domain.map.dto.LocationDto;
import com.example.log4u.domain.map.entitiy.Location;

public class LocationFixture {

	// 기본 위치 생성
	public static Location createDefaultLocation() {
		return Location.builder()
			.latitude(37.5665)
			.longitude(126.9780)
			.sido("서울특별시")
			.sigungu("중구")
			.eupmyeondong("명동")
			.build();
	}

	// 커스텀 위치 생성
	public static Location createCustomLocation(
		Double latitude,
		Double longitude,
		String sido,
		String sigungu,
		String eupmyeondong
	) {
		return Location.builder()
			.latitude(latitude)
			.longitude(longitude)
			.sido(sido)
			.sigungu(sigungu)
			.eupmyeondong(eupmyeondong)
			.build();
	}

	// 서울 강남 위치 생성
	public static Location createGangnamLocation() {
		return Location.builder()
			.latitude(37.4979)
			.longitude(127.0276)
			.sido("서울특별시")
			.sigungu("강남구")
			.eupmyeondong("삼성동")
			.build();
	}

	// 서울 홍대 위치 생성
	public static Location createHongdaeLocation() {
		return Location.builder()
			.latitude(37.5582)
			.longitude(126.9267)
			.sido("서울특별시")
			.sigungu("마포구")
			.eupmyeondong("서교동")
			.build();
	}

	// 부산 해운대 위치 생성
	public static Location createHaeundaeLocation() {
		return Location.builder()
			.latitude(35.1586)
			.longitude(129.1603)
			.sido("부산광역시")
			.sigungu("해운대구")
			.eupmyeondong("우동")
			.build();
	}

	// 제주 위치 생성
	public static Location createJejuLocation() {
		return Location.builder()
			.latitude(33.4996)
			.longitude(126.5312)
			.sido("제주특별자치도")
			.sigungu("제주시")
			.eupmyeondong("일도동")
			.build();
	}

	// 기본 LocationDto 생성
	public static LocationDto createDefaultLocationDto() {
		return LocationDto.builder()
			.latitude(37.5665)
			.longitude(126.9780)
			.sido("서울특별시")
			.sigungu("중구")
			.eupmyeondong("명동")
			.build();
	}

	// 커스텀 LocationDto 생성
	public static LocationDto createCustomLocationDto(
		Double latitude,
		Double longitude,
		String sido,
		String sigungu,
		String eupmyeondong
	) {
		return LocationDto.builder()
			.latitude(latitude)
			.longitude(longitude)
			.sido(sido)
			.sigungu(sigungu)
			.eupmyeondong(eupmyeondong)
			.build();
	}

	// 서울 강남 LocationDto 생성
	public static LocationDto createGangnamLocationDto() {
		return LocationDto.builder()
			.latitude(37.4979)
			.longitude(127.0276)
			.sido("서울특별시")
			.sigungu("강남구")
			.eupmyeondong("삼성동")
			.build();
	}

	// 서울 홍대 LocationDto 생성
	public static LocationDto createHongdaeLocationDto() {
		return LocationDto.builder()
			.latitude(37.5582)
			.longitude(126.9267)
			.sido("서울특별시")
			.sigungu("마포구")
			.eupmyeondong("서교동")
			.build();
	}

	// 부산 해운대 LocationDto 생성
	public static LocationDto createHaeundaeLocationDto() {
		return LocationDto.builder()
			.latitude(35.1586)
			.longitude(129.1603)
			.sido("부산광역시")
			.sigungu("해운대구")
			.eupmyeondong("우동")
			.build();
	}

	// 제주 LocationDto 생성
	public static LocationDto createJejuLocationDto() {
		return LocationDto.builder()
			.latitude(33.4996)
			.longitude(126.5312)
			.sido("제주특별자치도")
			.sigungu("제주시")
			.eupmyeondong("일도동")
			.build();
	}
}