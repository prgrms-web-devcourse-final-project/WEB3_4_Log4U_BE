package com.example.log4u.domain.diary.entity;

import java.util.ArrayList;
import java.util.List;

import com.example.log4u.domain.comment.Comment;
import com.example.log4u.domain.diary.VisibilityType;
import com.example.log4u.domain.diary.dto.DiaryRequestDto;
import com.example.log4u.domain.like.entity.Like;
import com.example.log4u.domain.media.entity.Media;
import com.example.log4u.domain.user.entity.User;
import com.example.log4u.global.entity.BaseTimeEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Diary extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	private String title;

	private String content;

	private Double latitude;

	private Double longitude;

	private String weatherInfo;

	@Enumerated(EnumType.STRING)
	private VisibilityType visibility;

	private String thumbnailUrl;

	@Builder.Default
	@OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Media> media = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Comment> comments = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "diary", orphanRemoval = true)
	private List<Like> likes = new ArrayList<>();

	@Builder.Default
	private int likesCount = 0;

	public static Diary toEntity(DiaryRequestDto request) {
		return Diary.builder()
			.title(request.title())
			.content(request.content())
			.latitude(request.latitude())
			.longitude(request.longitude())
			.weatherInfo(request.weatherInfo())
			.visibility(VisibilityType.valueOf(request.visibility()))
			.build();
	}

	public void update(DiaryRequestDto request) {
		this.title = request.title();
		this.content = request.content();
		this.latitude = request.latitude();
		this.longitude = request.longitude();
		this.weatherInfo = request.weatherInfo();
		this.visibility = VisibilityType.valueOf(request.visibility());
		// TODO: Media 업데이트
	}

	public void addMedia(List<Media> mediaList) {
		if (mediaList == null || mediaList.isEmpty()) {
			return;
		}
		mediaList.forEach(media -> {
			this.media.add(media);
			media.setDiary(this);
		});

		this.thumbnailUrl = mediaList.get(0).getUrl();
	}

	public void addComment(Comment comment) {
		comments.add(comment);
		comment.setDiary(this);
	}

	public void addUser(User user) {
		this.user = user;
		user.getDiaries().add(this);
	}

	public void addLike(Like like) {
		this.likes.add(like);
		like.setDiary(this);
	}

	public void removeLike(Like like) {
		this.likes.remove(like);
	}
}
