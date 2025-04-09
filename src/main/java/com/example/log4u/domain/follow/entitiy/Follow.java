package com.example.log4u.domain.follow.entitiy;

import com.example.log4u.common.entity.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "Follow")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 팔로우를 누른 주체
	private Long initiatorId;

	// 팔로우를 받은 대상
	private Long targetId;

	// dto 넣기 애매해서 엔티티 내부에 static 함수 구현
	public static Follow of(Long initiatorId, Long targetId) {
		return new Follow(
			null,
			initiatorId,
			targetId
		);
	}
}
