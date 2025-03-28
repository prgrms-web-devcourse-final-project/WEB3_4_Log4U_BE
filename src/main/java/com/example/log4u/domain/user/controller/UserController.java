package com.example.log4u.domain.user.controller;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.log4u.common.oauth2.dto.CustomOAuth2User;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

	@GetMapping("")
	public String modifyUserProfile(
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User
	){
		log.info("테스트 GET DATA user = " + customOAuth2User.getUserId() );
		return "test";
	}
}
