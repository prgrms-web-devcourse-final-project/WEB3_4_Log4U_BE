package com.example.log4u.domain.map.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sido_areas_diary_count", schema = "public")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SidoAreasDiaryCount {

	@Id
	@Column(name = "id")
	private Long id;

	@Column(name = "diary_count")
	private Long diaryCount;


	public void incrementCount() {
		this.diaryCount++;
	}
}
