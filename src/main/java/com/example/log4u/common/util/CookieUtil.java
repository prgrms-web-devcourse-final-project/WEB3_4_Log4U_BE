package com.example.log4u.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

	private CookieUtil() {
	}

	public static Cookie createCookie(String key, String value) {
		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(60 * 60 * 60);
		//cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		return cookie;
	}

	public static void deleteCookie(HttpServletResponse response) {
		Cookie access = CookieUtil.createCookie("access", null);
		Cookie refresh = CookieUtil.createCookie("refresh", null);

		access.setMaxAge(0);
		access.setPath("/");
		refresh.setMaxAge(0);
		refresh.setPath("/");

		response.addCookie(access);
		response.addCookie(refresh);
		response.setStatus(HttpServletResponse.SC_OK);
	}
}
