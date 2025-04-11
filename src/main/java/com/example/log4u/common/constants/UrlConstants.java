package com.example.log4u.common.constants;

public class UrlConstants {

	public static final String FRONT_ORIGIN_URL = "http://web.ec2-13-209-127-186.ap-northeast-2.compute.amazonaws.com:3000/me";
	public static final String FRONT_SUB_DOMAIN_URL = "http://web.ec2-13-209-127-186.ap-northeast-2.compute.amazonaws.com:3000";
	public static final String FRONT_VERCEL_ORIGIN = "https://web-3-4-log4-u-fe.vercel.app";
	// 임시로 메인
	public static final String PROFILE_CREATE_URL = FRONT_VERCEL_ORIGIN + "";
	public static final String LOGIN_URL = FRONT_VERCEL_ORIGIN + "/login";

	// 내 프로필 페이지가 메인
	public static final String MAIN_URL = FRONT_VERCEL_ORIGIN + "/me";

	// checkstyle 경고 제거
	private UrlConstants() {
	}
}

