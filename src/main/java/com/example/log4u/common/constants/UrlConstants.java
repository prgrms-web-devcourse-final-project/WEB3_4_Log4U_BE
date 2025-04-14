package com.example.log4u.common.constants;

public class UrlConstants {

	public static final String FRONT_ORIGIN_URL = " https://web.log4u.site:3000";
	public static final String FRONT_SUB_DOMAIN_URL = "https://web.log4u.site:3000";
	public static final String FRONT_VERCEL_ORIGIN = "https://fe-log4u.vercel.app";

	// 임시로 메인
	public static final String PROFILE_CREATE_URL = FRONT_VERCEL_ORIGIN + "";
	public static final String LOGIN_URL = FRONT_VERCEL_ORIGIN + "login";

	// 내 프로필 페이지가 메인
	public static final String MAIN_URL = FRONT_VERCEL_ORIGIN + "me";

	// checkstyle 경고 제거
	private UrlConstants() {
	}
}

