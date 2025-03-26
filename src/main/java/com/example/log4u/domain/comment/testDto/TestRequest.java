package com.example.log4u.domain.comment.testDto;

// dto/TestRequest.java
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TestRequest {

	@NotBlank
	private String name;

	@Min(value = 18, message = "나이는 18세 이상이어야 합니다.")
	private int age;

}
