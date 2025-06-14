package com.example.log4u.domain.diary.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.log4u.domain.diary.entity.DiaryGeoHash;
import com.example.log4u.domain.diary.repository.DiaryGeoHashRepository;
import com.example.log4u.domain.map.cache.dao.DiaryCacheDao;

import ch.hsr.geohash.GeoHash;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiaryGeohashService {

	private final DiaryGeoHashRepository diaryGeoHashRepository;
	private final DiaryCacheDao diaryCacheDao;

	@Transactional
	public void saveGeohash(Long diaryId, double lat, double lon) {
		String hash = GeoHash.withCharacterPrecision(lat, lon, 5).toBase32();
		DiaryGeoHash diaryGeoHash = DiaryGeoHash.builder()
			.diaryId(diaryId)
			.geohash(hash)
			.build();
		diaryGeoHashRepository.save(diaryGeoHash);
	}

	@Transactional
	public void deleteGeohashAndCache(Long diaryId) {
		DiaryGeoHash geoHash = diaryGeoHashRepository.findByDiaryId(diaryId);
		diaryCacheDao.evictDiaryIdFromCache(geoHash.getGeohash(), diaryId);
		diaryCacheDao.evictDiaryFromCache(diaryId);

		diaryGeoHashRepository.deleteById(diaryId);
	}

	public List<Long> getDiaryIdsByGeohash(String geohash) {
		return diaryGeoHashRepository.findDiaryIdByGeohash(geohash);
	}
}
