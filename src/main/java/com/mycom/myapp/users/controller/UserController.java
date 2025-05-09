package com.mycom.myapp.users.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycom.myapp.users.dto.UserDto;
import com.mycom.myapp.users.dto.UserResultDto;
import com.mycom.myapp.users.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
	
	private final UserService userService;
	
	@PostMapping("/users")
	public ResponseEntity<UserResultDto> registerUser(UserDto userDto) {
		UserResultDto userResultDto = userService.registerUser(userDto);
		
		System.out.println("/users");
		
		if("success".equals(userResultDto.getResult())) {
			return ResponseEntity.ok(userResultDto);
		} 
		else if("exist".equals(userResultDto.getResult())) {
			return ResponseEntity.badRequest().body(userResultDto);
		}
		else {
			return ResponseEntity.internalServerError().body(userResultDto);
		}
	}
	
	
}
