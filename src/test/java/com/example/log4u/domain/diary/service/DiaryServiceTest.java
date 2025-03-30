package com.example.log4u.domain.diary.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.log4u.domain.diary.repository.DiaryRepository;
import com.example.log4u.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class DiaryServiceTest {

	@Mock
	DiaryRepository diaryRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private DiaryService diaryService;

	@Test
	@DisplayName("다이어리 생성 - 성공")
	void createDiary_success() {
		/*// given
		DiaryRequestDto request = new DiaryRequestDto(
			"제목",
			"내용",
			37.5665,
			34.2121,
			WeatherInfo.SUNNY.name(),
			VisibilityType.PUBLIC.name()
		);

		User user = new User();
		Diary*/
	}
}
