package com.example.log4u.domain.map.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sigg_areas_diary_count", schema = "public")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SiggAreasDiaryCount {

	@Id
	@Column(name = "id")
	private Long id;

	@Column(name = "diary_count", nullable = false)
	private Long diaryCount;

	public void incrementCount() {
		this.diaryCount++;
	}

	public void decrementCount() {
		this.diaryCount = Math.max(0, this.diaryCount - 1);
	}
}
