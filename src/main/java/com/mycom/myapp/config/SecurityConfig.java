package com.mycom.myapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http,
			LoginAuthenticationSuccessHandler successHandler, LoginAuthenticationFailureHandler failureHandler) throws Exception {
		return http
				.authorizeHttpRequests(
					request -> {
						request.requestMatchers("/",
								"/index.html",
								"/api/auth/csrf-token", // csrf token
								"/login", // login
								"/login.html",
								"/register", // register
								"/register.html",
		                        "/v3/api-docs/**", // swagger
		                        "/swagger-ui/**",
		                        "/swagger-ui.html"
//		                        ,"/api/**" // Postman API 테스트용
						)
						.permitAll();
						request.requestMatchers(HttpMethod.POST, "/api/users")
						.permitAll()
						.anyRequest().authenticated();
					}	
				)
				.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())) // Cookie -> CSRF
//				.csrf(csrf -> csrf.disable()) // Postman API 테스트용
				.formLogin(
					form -> form
					.loginPage("/login.html")
					.loginProcessingUrl("/login") // 로그인 처리 URL
					.successHandler(successHandler) // 로그인 성공 처리
					.failureHandler(failureHandler) // 로그인 실패 처리
					.permitAll()
				)
				.logout(logout -> logout.permitAll()) // Logout -> Session Invalidate
				.build();
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
