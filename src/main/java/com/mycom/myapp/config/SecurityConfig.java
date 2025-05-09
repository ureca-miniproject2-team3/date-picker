package com.mycom.myapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
								"/csrf-token",
								"/login",
								"/register",
								"/register.html"
						)
						.permitAll()
						.anyRequest().authenticated();
					}	
				)
				.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())) // Cookie -> CSRF
				.formLogin(
					form -> form
					.loginPage("/login.html")
					.loginProcessingUrl("/login") // 로그인 처리 URL
					.successHandler(successHandler) // 로그인 성공 처리
					.failureHandler(failureHandler) // 로그인 실패 처리
					.defaultSuccessUrl("/", true)
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
