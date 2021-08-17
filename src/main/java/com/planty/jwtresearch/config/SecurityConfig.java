package com.planty.jwtresearch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.planty.jwtresearch.jwt.JwtAccessDeniedHandler;
import com.planty.jwtresearch.jwt.JwtAuthenticationEntryPoint;
import com.planty.jwtresearch.jwt.JwtSecurityConfig;
import com.planty.jwtresearch.jwt.TokenProvider;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // 메소드 단위로 @PreAuthorize 검증 어노테이션을 사용하기 위해 추가
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	private final TokenProvider tokenProvider;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

	public SecurityConfig(TokenProvider tokenProvider, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
			JwtAccessDeniedHandler jwtAccessDeniedHandler) {
		this.tokenProvider = tokenProvider;
		this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
		this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	public void configure(WebSecurity web) {
		web.ignoring() // Spring Security 로직 수행하지 않음
				.antMatchers( // 대상은 Spring Security 로직 수행 안함
						"/h2-console/**" // http://localhost:8080/h2-console
						, "/favicon.ico");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.csrf().disable()	// Token 방식이므로, CSRF 해제

				.exceptionHandling()
				.authenticationEntryPoint(jwtAuthenticationEntryPoint)	// JwtAuthenticationEntryPoint 주입 (유효한 자격증명을 제공하지 않고 접근하려 할때 401 UNAUTHORIZED 에러)
				.accessDeniedHandler(jwtAccessDeniedHandler)			// JwtAccessDeniedHandler 주입 (필요한 권한이 존재하지 않은 경우 403 FORBIDDEN 에러)

				.and()
				.headers()
				.frameOptions()
				.sameOrigin()	// 데이터 확인을 위해 사용하고 있는 h2-console을 위한 설정을 추가

				.and()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)	// 세션을 사용하지 않기 때문에 세션 설정을 STATELESS로 지정

				.and()
				.authorizeRequests()
				.antMatchers("/api/hello").permitAll()
				.antMatchers("/api/authenticate").permitAll()	// 로그인   API는 Token이 없어도 호출할 수 있도록 허용
				.antMatchers("/api/signup").permitAll()			// 회원가입 API는 Token이 없어도 호출할 수 있도록 허용
				.anyRequest().authenticated()

				.and()
				.apply(new JwtSecurityConfig(tokenProvider));	// TokenProvider 주입 (토큰생성)
	}

}