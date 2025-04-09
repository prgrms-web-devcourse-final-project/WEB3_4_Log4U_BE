package com.example.log4u.common.constants;

public class UrlConstants {

	public static final String FRONT_ORIGIN_URL = "http://localhost:3000/me";
	public static final String FRONT_SUB_DOMAIN_URL = "http://web.ec2-13-209-127-186.ap-northeast-2.compute.amazonaws.com:3000";
	public static final String PROFILE_CREATE_URL = FRONT_SUB_DOMAIN_URL + "/profile/make";
	public static final String LOGIN_URL = FRONT_SUB_DOMAIN_URL + "/login";

	// 내 프로필 페이지가 메인
	public static final String MAIN_URL = FRONT_SUB_DOMAIN_URL;

	// checkstyle 경고 제거
	private UrlConstants() {
	}
}
