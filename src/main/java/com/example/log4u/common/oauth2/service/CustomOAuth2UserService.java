package com.example.log4u.common.oauth2.service;

import java.util.Optional;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.log4u.common.oauth2.dto.CustomOAuth2User;
import com.example.log4u.common.oauth2.dto.GoogleResponseDto;
import com.example.log4u.common.oauth2.dto.KakaoResponseDto;
import com.example.log4u.common.oauth2.dto.NaverResponseDto;
import com.example.log4u.common.oauth2.dto.OAuth2Response;
import com.example.log4u.common.oauth2.dto.UserCreateRequestDto;
import com.example.log4u.domain.user.entity.User;
import com.example.log4u.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
	private final UserRepository userRepository;

	/**
	 * OAuth2 인증 후 리소스 서버에서 받아온 아온 사용자 정보 처리
	 * <ol>
	 *     <li>OAuth2UserService 를 통해 사용자 정보 조회</li>
	 *     <li>CustomOAuth2User 반환(to security manager)</li>
	 * </ol>
	 */
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		String registrationId = userRequest.getClientRegistration().getRegistrationId();

		// 정보 가공
		OAuth2Response oAuth2Response = switch (registrationId) {
			case "naver" -> new NaverResponseDto(oAuth2User.getAttributes());
			case "google" -> new GoogleResponseDto(oAuth2User.getAttributes());
			case "kakao" -> new KakaoResponseDto(oAuth2User.getAttributes());
			default -> throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인");
		};

		// 정보 조회
		String providerId = oAuth2Response.getProviderId();
		Optional<User> dbUser = userRepository.findByProviderId(providerId);

		// 첫 로그인이면 프로필 없으므로 우선 GUEST 설정
		if (dbUser.isEmpty()) {
			return createUser(oAuth2Response);
		} else { // DB의 유저 정보 갱신
			return updateUser(oAuth2Response, dbUser.get());
		}
	}

	public CustomOAuth2User createUser(OAuth2Response oAuth2Response) {
		UserCreateRequestDto userCreateRequestDto = UserCreateRequestDto.fromOAuth2Response(
			oAuth2Response,
			null,
			"ROLE_GUEST"
		);
		User user = UserCreateRequestDto.toEntity(userCreateRequestDto);
		userRepository.save(user);

		UserCreateRequestDto afterSaveDto = UserCreateRequestDto.fromOAuth2Response(
			oAuth2Response,
			user.getUserId(),
			"ROLE_GUEST"
		);

		return new CustomOAuth2User(afterSaveDto);
	}

	public CustomOAuth2User updateUser(OAuth2Response oAuth2Response, User user) {
		user.updateOauth2Profile(oAuth2Response);
		userRepository.save(user);

		UserCreateRequestDto userCreateRequestDto = UserCreateRequestDto.fromOAuth2Response(
			oAuth2Response,
			user.getUserId(),
			user.getRole()
		);
		return new CustomOAuth2User(userCreateRequestDto);
	}
}
