package com.example.log4u.domain.Map.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.log4u.common.ServiceTest;
import com.example.log4u.domain.diary.VisibilityType;
import com.example.log4u.domain.diary.WeatherInfo;
import com.example.log4u.domain.diary.entity.Diary;
import com.example.log4u.domain.diary.entity.DiaryGeoHash;
import com.example.log4u.domain.diary.entity.Location;
import com.example.log4u.domain.diary.repository.DiaryGeoHashRepository;
import com.example.log4u.domain.diary.repository.DiaryRepository;
import com.example.log4u.domain.map.cache.ClustersCacheService;
import com.example.log4u.domain.map.cache.MarkersCacheService;
import com.example.log4u.domain.map.dto.response.GetDiaryClusterResponse;
import com.example.log4u.domain.map.dto.response.GetDiaryMarkerResponse;
import com.example.log4u.domain.map.entity.SidoAreas;
import com.example.log4u.domain.map.entity.SidoAreasDiaryCount;
import com.example.log4u.domain.map.entity.SiggAreas;
import com.example.log4u.domain.map.entity.SiggAreasDiaryCount;
import com.example.log4u.domain.map.repository.sido.SidoAreasDiaryCountRepository;
import com.example.log4u.domain.map.repository.sido.SidoAreasRepository;
import com.example.log4u.domain.map.repository.sigg.SiggAreasDiaryCountRepository;
import com.example.log4u.domain.map.repository.sigg.SiggAreasRepository;

public class MapCacheServiceTest extends ServiceTest {

	@Autowired
	private ClustersCacheService clustersCacheService;

	@Autowired
	private MarkersCacheService markersCacheService;

	@Autowired
	private SidoAreasRepository sidoAreasRepository;

	@Autowired
	private SiggAreasRepository siggAreasRepository;

	@Autowired
	private SidoAreasDiaryCountRepository sidoAreasDiaryCountRepository;

	@Autowired
	private SiggAreasDiaryCountRepository siggAreasDiaryCountRepository;

	@Autowired
	private DiaryRepository diaryRepository;

	@Autowired
	private DiaryGeoHashRepository diaryGeoHashRepository;

	@DisplayName("시/도 클러스터 목록을 캐싱한다")
	@Test
	void cacheSidoClusters() {
		// given
		String geohash = "wyd";
		int level = 1;

		SidoAreas sidoAreas = SidoAreas.builder()
			.id(1L)
			.name("서울")
			.code("11")
			.lat(37.5665)
			.lon(126.9780)
			.geohash("wyd")
			.build();

		SidoAreasDiaryCount sidoAreasDiaryCount = SidoAreasDiaryCount.builder()
			.id(sidoAreas.getId())
			.diaryCount(125L)
			.build();

		sidoAreasRepository.save(sidoAreas);
		sidoAreasDiaryCountRepository.save(sidoAreasDiaryCount);

		clustersCacheService.refresh(geohash, level);

		// when
		List<GetDiaryClusterResponse> clusters = clustersCacheService.getClusters(geohash, level);

		// then
		assertThat(clusters).isNotNull();
		assertThat(clusters)
			.extracting(
				GetDiaryClusterResponse::areaId,
				GetDiaryClusterResponse::areaName,
				GetDiaryClusterResponse::lat,
				GetDiaryClusterResponse::lon,
				GetDiaryClusterResponse::diaryCount)
			.contains(tuple(
				sidoAreas.getId(),
				sidoAreas.getName(),
				sidoAreas.getLat(),
				sidoAreas.getLon(),
				sidoAreasDiaryCount.getDiaryCount()));
	}

	@DisplayName("캐싱된 시/도 클러스터 목록을 조회한다.")
	@Test
	void loadSidoClustersFromRedis() {
		// given
		String geohash = "wyd";
		int level = 1;

		SidoAreas sidoAreas1 = SidoAreas.builder()
			.id(1L)
			.name("서울")
			.code("11")
			.lat(37.5665)
			.lon(126.9780)
			.geohash("wyd")
			.build();

		SidoAreas sidoAreas2 = SidoAreas.builder()
			.id(2L)
			.name("경기도")
			.code("41")
			.lat(37.4138)
			.lon(127.5183)
			.geohash("wyd")
			.build();

		SidoAreasDiaryCount sidoAreasDiaryCount1 = SidoAreasDiaryCount.builder()
			.id(sidoAreas1.getId())
			.diaryCount(125L)
			.build();

		SidoAreasDiaryCount sidoAreasDiaryCount2 = SidoAreasDiaryCount.builder()
			.id(sidoAreas2.getId())
			.diaryCount(87L)
			.build();

		sidoAreasRepository.save(sidoAreas1);
		sidoAreasRepository.save(sidoAreas2);
		sidoAreasDiaryCountRepository.save(sidoAreasDiaryCount1);
		sidoAreasDiaryCountRepository.save(sidoAreasDiaryCount2);

		clustersCacheService.refresh(geohash, level);

		// when
		List<GetDiaryClusterResponse> clusters = clustersCacheService.getClusters(geohash, level);

		// then
		assertThat(clusters).isNotNull();
		assertThat(clusters)
			.extracting(
				GetDiaryClusterResponse::areaId,
				GetDiaryClusterResponse::areaName,
				GetDiaryClusterResponse::lat,
				GetDiaryClusterResponse::lon,
				GetDiaryClusterResponse::diaryCount)
			.containsExactlyInAnyOrder(
				tuple(
					sidoAreas1.getId(),
					sidoAreas1.getName(),
					sidoAreas1.getLat(),
					sidoAreas1.getLon(),
					sidoAreasDiaryCount1.getDiaryCount()),
				tuple(
					sidoAreas2.getId(),
					sidoAreas2.getName(),
					sidoAreas2.getLat(),
					sidoAreas2.getLon(),
					sidoAreasDiaryCount2.getDiaryCount()));
	}

	@DisplayName("시/군/구 클러스터 목록을 캐싱한다")
	@Test
	void cacheSiggClusters() {
		//given
		String geohash = "wyd";
		int level = 2;

		SiggAreas siggAreas = SiggAreas.builder()
			.gid(1L)
			.sggName("중구")
			.lat(37.5636)
			.lon(126.9970)
			.geohash("wyd")
			.build();

		SiggAreasDiaryCount siggDiaryCount = SiggAreasDiaryCount.builder()
			.id(siggAreas.getGid())
			.diaryCount(55L)
			.build();

		siggAreasRepository.save(siggAreas);
		siggAreasDiaryCountRepository.save(siggDiaryCount);

		clustersCacheService.refresh(geohash, level);

		// when
		List<GetDiaryClusterResponse> clusters = clustersCacheService.getClusters(geohash, level);

		// then
		assertThat(clusters).isNotNull();
		assertThat(clusters)
			.extracting(
				GetDiaryClusterResponse::areaId,
				GetDiaryClusterResponse::areaName,
				GetDiaryClusterResponse::lat,
				GetDiaryClusterResponse::lon,
				GetDiaryClusterResponse::diaryCount)
			.contains(tuple(
					siggAreas.getGid(),
					siggAreas.getSggName(),
					siggAreas.getLat(),
					siggAreas.getLon(),
					siggDiaryCount.getDiaryCount()));
	}

	@DisplayName("캐싱된 시/군/구 클러스터 목록을 조회한다.")
	@Test
	void loadSiggClustersFromRedis() {
		//given
		String geohash = "wyd";
		int level = 2;

		SiggAreas siggAreas1 = SiggAreas.builder()
			.gid(1L)
			.sggName("중구")
			.lat(37.5636)
			.lon(126.9970)
			.geohash("wyd")
			.build();

		SiggAreas siggAreas2 = SiggAreas.builder()
			.gid(2L)
			.sggName("강남구")
			.lat(37.5172)
			.lon(127.0473)
			.geohash("wyd")
			.build();

		SiggAreasDiaryCount siggAreasDiaryCount1 = SiggAreasDiaryCount.builder()
			.id(siggAreas1.getGid())
			.diaryCount(55L)
			.build();

		SiggAreasDiaryCount siggAreasDiaryCount2 = SiggAreasDiaryCount.builder()
			.id(siggAreas2.getGid())
			.diaryCount(80L)
			.build();

		siggAreasRepository.save(siggAreas1);
		siggAreasRepository.save(siggAreas2);
		siggAreasDiaryCountRepository.save(siggAreasDiaryCount1);
		siggAreasDiaryCountRepository.save(siggAreasDiaryCount2);

		clustersCacheService.refresh(geohash, level);

		// when
		List<GetDiaryClusterResponse> clusters = clustersCacheService.getClusters(geohash, level);

		// then
		assertThat(clusters).isNotNull();
		assertThat(clusters)
			.extracting(
				GetDiaryClusterResponse::areaId,
				GetDiaryClusterResponse::areaName,
				GetDiaryClusterResponse::lat,
				GetDiaryClusterResponse::lon,
				GetDiaryClusterResponse::diaryCount)
			.containsExactlyInAnyOrder(
				tuple(
					siggAreas1.getGid(),
					siggAreas1.getSggName(),
					siggAreas1.getLat(),
					siggAreas1.getLon(),
					siggAreasDiaryCount1.getDiaryCount()),
				tuple(
					siggAreas2.getGid(),
					siggAreas2.getSggName(),
					siggAreas2.getLat(),
					siggAreas2.getLon(),
					siggAreasDiaryCount2.getDiaryCount()));
	}

	@DisplayName("마커 목록을 캐싱한다")
	@Test
	void cacheMarkers() {
		// given
		String geohash = "wydm5";

		Location location = Location.builder()
			.latitude(37.5665)
			.longitude(126.9780)
			.sido("서울")
			.sigungu("중구")
			.eupmyeondong("명동")
			.build();

		Diary diary = Diary.builder()
			.userId(1L)
			.diaryId(1L)
			.title("다이어리1")
			.thumbnailUrl("http://localhost:8080/thumb1")
			.content("내용1")
			.diaryDate(LocalDate.of(2026, 1, 1))
			.location(location)
			.weatherInfo(WeatherInfo.SUNNY)
			.visibility(VisibilityType.PUBLIC)
			.likeCount(3L)
			.build();

		DiaryGeoHash diaryGeoHash = DiaryGeoHash.builder()
			.id(1L)
			.diaryId(diary.getDiaryId())
			.geohash("wydm5")
			.build();

		diaryRepository.save(diary);
		diaryGeoHashRepository.save(diaryGeoHash);

		// when
		markersCacheService.refresh(geohash);

		// then
		List<Diary> markers = markersCacheService.getMarkers(geohash);
		assertThat(markers).isNotNull();
		assertThat(markers)
			.extracting(
				Diary::getDiaryId,
				Diary::getTitle,
				Diary::getThumbnailUrl,
				d -> d.getLocation().getLatitude(),
				d -> d.getLocation().getLongitude(),
				Diary::getLikeCount)
			.containsExactly(
				tuple(
					diary.getDiaryId(),
					diary.getTitle(),
					diary.getThumbnailUrl(),
					diary.getLocation().getLatitude(),
					diary.getLocation().getLongitude(),
					diary.getLikeCount()));
	}

	@DisplayName("캐싱된 마커 목록을 조회한다.")
	@Test
	void loadMarkersFromRedis() {
		// given
		String geohash = "wydm5";

		Location location1 = Location.builder()
			.latitude(37.5665)
			.longitude(126.9780)
			.sido("서울")
			.sigungu("중구")
			.eupmyeondong("명동")
			.build();

		Location location2 = Location.builder()
			.latitude(37.5650)
			.longitude(126.9760)
			.sido("서울")
			.sigungu("중구")
			.eupmyeondong("을지로")
			.build();

		Diary diary1 = Diary.builder()
			.userId(1L)
			.diaryId(1L)
			.title("다이어리1")
			.thumbnailUrl("http://localhost:8080/thumb1")
			.content("내용1")
			.diaryDate(LocalDate.of(2026, 1, 1))
			.location(location1)
			.weatherInfo(WeatherInfo.SUNNY)
			.visibility(VisibilityType.PUBLIC)
			.likeCount(3L)
			.build();

		Diary diary2 = Diary.builder()
			.userId(2L)
			.diaryId(2L)
			.title("다이어리2")
			.thumbnailUrl("http://localhost:8080/thumb2")
			.content("내용2")
			.diaryDate(LocalDate.of(2026, 1, 2))
			.location(location2)
			.weatherInfo(WeatherInfo.CLOUDY)
			.visibility(VisibilityType.PUBLIC)
			.likeCount(10L)
			.build();

		DiaryGeoHash diaryGeoHash1 = DiaryGeoHash.builder()
			.id(1L)
			.diaryId(diary1.getDiaryId())
			.geohash("wydm5")
			.build();

		DiaryGeoHash diaryGeoHash2 = DiaryGeoHash.builder()
			.id(2L)
			.diaryId(diary2.getDiaryId())
			.geohash("wydm5")
			.build();

		diaryRepository.save(diary1);
		diaryRepository.save(diary2);
		diaryGeoHashRepository.save(diaryGeoHash1);
		diaryGeoHashRepository.save(diaryGeoHash2);

		markersCacheService.refresh(geohash);

		// when
		List<Diary> markers = markersCacheService.getMarkers(geohash);

		// then
		assertThat(markers).isNotNull();
		assertThat(markers)
			.extracting(
				Diary::getDiaryId,
				Diary::getTitle,
				Diary::getThumbnailUrl,
				d -> d.getLocation().getLatitude(),
				d -> d.getLocation().getLongitude(),
				Diary::getLikeCount)
			.containsExactlyInAnyOrder(
				tuple(
					diary1.getDiaryId(),
					diary1.getTitle(),
					diary1.getThumbnailUrl(),
					diary1.getLocation().getLatitude(),
					diary1.getLocation().getLongitude(),
					diary1.getLikeCount()),
				tuple(
					diary2.getDiaryId(),
					diary2.getTitle(),
					diary2.getThumbnailUrl(),
					diary2.getLocation().getLatitude(),
					diary2.getLocation().getLongitude(),
					diary2.getLikeCount()));
	}

	@DisplayName("캐싱된 마커 목록을 조회하여 좋아요 순으로 정렬된 300개 미만의 마커 목록을 반환한다 ")
	@Test
	void getTopLikedMarkers() {
		// given
		String geohash = "wydm5";

		Location location = Location.builder()
			.latitude(37.5665)
			.longitude(126.9780)
			.sido("서울")
			.sigungu("중구")
			.eupmyeondong("명동")
			.build();

		Diary d1 = Diary.builder()
			.userId(1L).diaryId(1L)
			.title("다이어리1")
			.thumbnailUrl("http://localhost:8080/thumb1")
			.content("내용1")
			.diaryDate(LocalDate.of(2026, 1, 1))
			.location(location)
			.weatherInfo(WeatherInfo.SUNNY)
			.visibility(VisibilityType.PUBLIC)
			.likeCount(3L)
			.build();

		Diary d2 = Diary.builder()
			.userId(2L).diaryId(2L)
			.title("다이어리2")
			.thumbnailUrl("http://localhost:8080/thumb2")
			.content("내용2")
			.diaryDate(LocalDate.of(2026, 1, 2))
			.location(location)
			.weatherInfo(WeatherInfo.CLOUDY)
			.visibility(VisibilityType.PUBLIC)
			.likeCount(10L)
			.build();

		Diary d3 = Diary.builder()
			.userId(3L).diaryId(3L)
			.title("다이어리3")
			.thumbnailUrl("http://localhost:8080/thumb3")
			.content("내용3")
			.diaryDate(LocalDate.of(2026, 1, 3))
			.location(location)
			.weatherInfo(WeatherInfo.RAINY)
			.visibility(VisibilityType.PUBLIC)
			.likeCount(7L)
			.build();

		diaryRepository.saveAll(List.of(d1, d2, d3));
		diaryGeoHashRepository.saveAll(List.of(
			DiaryGeoHash.builder().id(1L).diaryId(1L).geohash(geohash).build(),
			DiaryGeoHash.builder().id(2L).diaryId(2L).geohash(geohash).build(),
			DiaryGeoHash.builder().id(3L).diaryId(3L).geohash(geohash).build()
		));

		markersCacheService.refresh(geohash);

		// when
		List<Diary> result = markersCacheService.getTopLikedMarkers(geohash);

		// then
		assertThat(result).hasSize(3);
		assertThat(result)
			.extracting(Diary::getDiaryId, Diary::getLikeCount)
			.containsExactly(
				tuple(2L, 10L),
				tuple(3L, 7L),
				tuple(1L, 3L)
			);
	}

}
