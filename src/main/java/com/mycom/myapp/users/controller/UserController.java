package com.mycom.myapp.users.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mycom.myapp.users.dto.UserDto;
import com.mycom.myapp.users.dto.UserResultDto;
import com.mycom.myapp.users.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name="User", description="유저 관련 API")
public class UserController {
	
	private final UserService userService;
	
	@PostMapping("/users")
	@Operation(summary="회원 가입", description="신규 회원을 등록합니다.")
	public ResponseEntity<UserResultDto> registerUser(UserDto userDto) {
		UserResultDto userResultDto = userService.registerUser(userDto);
		
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
	
	@GetMapping("/users")
	@Operation(summary="회원 목록 조회", description="전체 회원 목록을 조회합니다.")
	public ResponseEntity<UserResultDto> listUser() {
		UserResultDto userResultDto = userService.listUser();
		
		if("success".equals(userResultDto.getResult())) {
			return ResponseEntity.ok(userResultDto);
		}
		else {
			return ResponseEntity.internalServerError().body(userResultDto);
		}
	}
	
	@GetMapping("/users/{userId}")
	@Operation(summary="회원 ID를 통한 상세 조회", description="특정 회원을 ID 값으로 조회합니다.")
	public ResponseEntity<UserResultDto> detailUser(@PathVariable("userId") Long userId) {
		UserResultDto userResultDto = userService.detailUser(userId);
		
		if("success".equals(userResultDto.getResult())) {
			return ResponseEntity.ok(userResultDto);
		}
		else {
			return ResponseEntity.internalServerError().body(userResultDto);
		}
	}
	
	@GetMapping("/users/search")
	@Operation(summary="회원 이메일을 통한 상세 조회", description="특정 회원을 이메일로 조회합니다.")
	public ResponseEntity<UserResultDto> detailUserByEmail(@RequestParam("email") String email) {
		UserResultDto userResultDto = userService.detailUserByEmail(email);
		
		if("success".equals(userResultDto.getResult())) {
			return ResponseEntity.ok(userResultDto);
		}
		else {
			return ResponseEntity.internalServerError().body(userResultDto);
		}
	}
}
