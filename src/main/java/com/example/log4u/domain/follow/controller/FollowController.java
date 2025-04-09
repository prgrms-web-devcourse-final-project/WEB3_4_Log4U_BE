package com.example.log4u.domain.follow.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.log4u.common.oauth2.dto.CustomOAuth2User;
import com.example.log4u.domain.follow.service.FollowService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "팔로우 API")
@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class FollowController {
	private final FollowService followService;

	@PostMapping("/{nickname}/follow")
	public ResponseEntity<Void> createFollow(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
		@PathVariable("nickname") String nickname
	) {
		followService.createFollow(customOAuth2User.getUserId(), nickname);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{nickname}/follow")
	public ResponseEntity<Void> deleteFollow(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
		@PathVariable("nickname") String nickname
	) {
		followService.deleteFollow(customOAuth2User.getUserId(), nickname);
		return ResponseEntity.ok().build();
	}

}
