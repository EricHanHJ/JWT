
package com.planty.jwtresearch.controller;

import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.planty.jwtresearch.dto.LoginDto;
import com.planty.jwtresearch.dto.TokenDto;
import com.planty.jwtresearch.jwt.JwtFilter;
import com.planty.jwtresearch.jwt.TokenProvider;

@RestController
@RequestMapping("/api")
public class AuthController {
	private final TokenProvider tokenProvider;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;

	public AuthController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
		this.tokenProvider = tokenProvider;
		this.authenticationManagerBuilder = authenticationManagerBuilder;
	}

	@PostMapping("/authenticate")
	public ResponseEntity<TokenDto> authorize(@Valid @RequestBody LoginDto loginDto) {

		// 파라메터(이름, PW) 받아서 인증토큰 객체생성
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken); // .authenticate(authenticationToken); : CustomUserDetailService.loadUserByUsername() 이 실행됨
		SecurityContextHolder.getContext().setAuthentication(authentication);	// authentication 객체럴 SecurityContext에 저장

		String jwt = tokenProvider.createToken(authentication);	// JWT Token 생성

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);	// Response Header에 JWT Token 넣음

		return new ResponseEntity<>(new TokenDto(jwt), httpHeaders, HttpStatus.OK); // Response Body에도 JWT Token을 new TokenDto(jwt)를 이용해서 넣음
	}
}