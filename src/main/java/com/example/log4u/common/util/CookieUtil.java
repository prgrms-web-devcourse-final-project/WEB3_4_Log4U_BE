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

	public static void deleteCookie(HttpServletResponse response, String name) {
		Cookie cookie = new Cookie(name, null);
		cookie.setMaxAge(0);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setDomain("log4u.site");
		// SameSite 설정
		response.addHeader("Set-Cookie", String.format(
			"%s=; Max-Age=0; Path=/; Domain=log4u.site; HttpOnly; Secure; SameSite=None",
			name
		));
	}

}
