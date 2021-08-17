
package com.planty.jwtresearch.service;

import java.util.Collections;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.planty.jwtresearch.dto.UserDto;
import com.planty.jwtresearch.entity.Authority;
import com.planty.jwtresearch.entity.User;
import com.planty.jwtresearch.repository.UserRepository;
import com.planty.jwtresearch.util.SecurityUtil;

@Service
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	// 회원가입 로직 수행
	@Transactional
	public User signup(UserDto userDto) {
		if (userRepository.findOneWithAuthoritiesByUsername(userDto.getUsername()).orElse(null) != null) {
			throw new RuntimeException("이미 가입되어 있는 유저입니다.");
		}

		// 빌더 패턴의 장점
		// Authority 정보 생성
		Authority authority = Authority.builder()
				.authorityName("ROLE_USER") // ROLE_USER : 일반 사용자 권한 명칭
				.build();

		// User정보 생성
		User user = User.builder()
				.username(userDto.getUsername())
				.password(passwordEncoder.encode(userDto.getPassword()))
				.nickname(userDto.getNickname())
				.authorities(Collections.singleton(authority))
				.activated(true)
				.build();

		// Insert 저장
		return userRepository.save(user);
	}

	// 유저네임을 파라메터로 받아서 해당 유저 정보 조회
	@Transactional(readOnly = true)
	public Optional<User> getUserWithAuthorities(String username) {
		return userRepository.findOneWithAuthoritiesByUsername(username);
	}

	// 현재 SecurityContext의 유저 정보 조회
	@Transactional(readOnly = true)
	public Optional<User> getMyUserWithAuthorities() {
		return SecurityUtil.getCurrentUsername().flatMap(userRepository::findOneWithAuthoritiesByUsername);
	}
}