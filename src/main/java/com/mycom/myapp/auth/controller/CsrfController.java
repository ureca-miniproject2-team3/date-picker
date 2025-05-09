package com.mycom.myapp.auth.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth")
@Tag(name="Auth", description="인증 관련 API")
public class CsrfController {

	@GetMapping("/csrf-token")
	@Operation(summary="CSRF 토큰 반환", description="인증에 필요한 CSRF 토큰을 반환합니다.")
	public CsrfToken csrf(CsrfToken token) {
		return token;
	}
}
 