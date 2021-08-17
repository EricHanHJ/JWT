package com.planty.jwtresearch.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.planty.jwtresearch.entity.User;
import com.planty.jwtresearch.repository.UserRepository;

@Component("userDetailsService")
public class CustomUserDetailService implements UserDetailsService {
	private final UserRepository userRepository;

	public CustomUserDetailService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	// 로그인시 DB에서 유저정보 조회
	@Override
	@Transactional
	public UserDetails loadUserByUsername(final String username) {
		return userRepository.findOneWithAuthoritiesByUsername(username).map(user -> createUser(username, user))
				.orElseThrow(() -> new UsernameNotFoundException(username + " -> 데이터베이스에서 찾을 수 없습니다."));
	}

	// 조회 후 권한정보 가져와서 userdetails.User 객체 생성후 리턴
	private org.springframework.security.core.userdetails.User createUser(String username, User user) {
		if (!user.isActivated()) {
			throw new RuntimeException(username + " -> 활성화되어 있지 않습니다.");
		}
		List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
				.map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
				.collect(Collectors.toList());
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				grantedAuthorities);
	}
}