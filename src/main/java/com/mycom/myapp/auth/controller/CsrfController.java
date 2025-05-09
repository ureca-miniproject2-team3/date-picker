package com.mycom.myapp.auth.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class CsrfController {

	@GetMapping("/csrf-token")
	public CsrfToken csrf(CsrfToken token) {
		return token;
	}
}
 