package com.example.log4u.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

	private CookieUtil() {
	}

	public static Cookie createCookie(String key, String value) {
		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(60 * 60 * 60);
		cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		return cookie;
	}

	public static void createCookieWithSameSite(HttpServletResponse response, String key, String value) {
		Cookie cookie = createCookie(key, value);
		// SameSite=None 설정을 위한 추가 헤더
		String headerValue = String.format("%s=%s; Max-Age=%d; Path=%s; HttpOnly; Secure; SameSite=None",
			key, value, cookie.getMaxAge(), cookie.getPath());
		response.addHeader("Set-Cookie", headerValue);
	}

	public static void deleteCookie(HttpServletResponse response) {
		// access 쿠키 삭제 - 헤더만 사용
		String accessCookieString = "access=; Max-Age=0; Path=/; HttpOnly; Secure; SameSite=None";
		response.addHeader("Set-Cookie", accessCookieString);

		// refresh 쿠키 삭제 - 헤더만 사용
		String refreshCookieString = "refresh=; Max-Age=0; Path=/; HttpOnly; Secure; SameSite=None";
		response.addHeader("Set-Cookie", refreshCookieString);

		response.setStatus(HttpServletResponse.SC_OK);
	}

}
