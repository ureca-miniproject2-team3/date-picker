package com.mycom.myapp.users.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mycom.myapp.auth.dao.LoginDao;
import com.mycom.myapp.users.dao.UserDao;
import com.mycom.myapp.users.dto.UserDto;
import com.mycom.myapp.users.dto.UserResultDto;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@InjectMocks
	private UserServiceImpl userService;
	
	@Mock
	private UserDao userDao;
	
	@Mock 
	private LoginDao loginDao;
	
	@Mock
	private PasswordEncoder passwordEncoder; 
	
	@Test
	public void registerUserTest_Exist() {
		UserDto user1 = UserDto.builder()
					.name("aaa")
					.email("aaa@aaa.com")
					.password("aaa")
					.build();
		
		when(loginDao.findByEmail("aaa@aaa.com")).thenReturn(UserDto.builder().
				id(1L).
				name("bbb").
				email("aaa@aaa.com").
				password("aaa").
				build());
		
		UserResultDto userResultDto = userService.registerUser(user1);
		
		assertEquals("exist", userResultDto.getResult());
	}
	
	@Test
	public void registerUserTest_Success() {
		UserDto userDto = UserDto.builder()
				.name("aaa")
				.email("aaa@aaa.com")
				.password("aaa")
				.build();
		
		UserResultDto userResultDto = userService.registerUser(userDto);
		
		assertEquals("success", userResultDto.getResult());
	}
	
    @Test
    public void registerUserTest_Fail() {
        UserDto userDto = UserDto.builder()
                .name("aaa")
                .email("aaa@aaa.com")
                .password("aaa")
                .build();

        when(loginDao.findByEmail(anyString()))
                .thenThrow(new RuntimeException("DB error"));

        UserResultDto result = userService.registerUser(userDto);
        assertEquals("fail", result.getResult());
    }
}

