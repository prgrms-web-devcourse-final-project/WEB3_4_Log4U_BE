package com.example.log4u.domain.diary.entity;

import java.util.ArrayList;
import java.util.List;

import com.example.log4u.domain.comment.Comment;
import com.example.log4u.domain.media.entity.Media;
import com.example.log4u.domain.user.entity.User;
import com.example.log4u.global.entity.BaseTimeEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
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

	private String visibility;

	private String thumbnailUrl;

	@Builder.Default
	@OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Media> mediaList = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Comment> commentList = new ArrayList<>();

	public void addMediaList(List<Media> mediaList) {
		mediaList.forEach(media -> {
			this.mediaList.add(media);
			media.setDiary(this);
		});

		this.thumbnailUrl = mediaList.get(0).getUrl();
	}

	public void addComment(Comment comment) {
		commentList.add(comment);
		comment.setDiary(this);
	}

}
