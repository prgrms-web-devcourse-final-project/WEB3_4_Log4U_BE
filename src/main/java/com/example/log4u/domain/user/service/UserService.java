package com.example.log4u.domain.user.service;

import org.springframework.stereotype.Service;

import com.example.log4u.domain.user.entity.User;
import com.example.log4u.domain.user.exception.UserNotFoundException;
import com.example.log4u.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;

	public User getUserById(Long userId){
		return userRepository.findById(userId).orElseThrow(
			UserNotFoundException::new
		);
	}
}
