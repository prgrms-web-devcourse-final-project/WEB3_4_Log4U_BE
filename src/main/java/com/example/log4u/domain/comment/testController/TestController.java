package com.example.log4u.domain.comment.testController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.log4u.domain.comment.testDto.TestRequest;
import com.example.log4u.domain.comment.exception.NotFoundCommentException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/test")
public class TestController {

	@PostMapping("/valid")
	public ResponseEntity<Void> testValidation(@RequestBody @Valid TestRequest request) {
		return ResponseEntity.ok().build();
	}

	@GetMapping("/illegal")
	public String testIllegalArgument() {
		throw new IllegalArgumentException("잘못된 인자입니다!");
	}

	@GetMapping("/log4u")
	public String testLog4uException() {
		throw new NotFoundCommentException(); // 또는 임의의 ServiceException
	}

	//test
}
