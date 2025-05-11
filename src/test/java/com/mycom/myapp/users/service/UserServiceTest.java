package com.mycom.myapp.users.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import java.util.List;

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
	void registerUserTest_Exist() {
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
	void registerUserTest_Success() {
		UserDto userDto = UserDto.builder()
				.name("aaa")
				.email("aaa@aaa.com")
				.password("aaa")
				.build();
		
		UserResultDto userResultDto = userService.registerUser(userDto);
		
		assertEquals("success", userResultDto.getResult());
	}
	
    @Test
    void registerUserTest_Fail() {
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
    
    @Test
    void listUserTest_Success() {
        UserDto user1 = UserDto.builder()
                .name("aaa")
                .email("aaa@aaa.com")
                .password("aaa")
                .build();
        
        UserDto user2 = UserDto.builder()
                .name("bbb")
                .email("bbb@bbb.com")
                .password("bbb")
                .build();
        
        when(userDao.listUser())
        	.thenReturn(List.of(user1, user2));
        
        UserResultDto result = userService.listUser();
        assertEquals("success", result.getResult());
        assertEquals(2, result.getUserDtoList().size());
    }
    
    @Test
    void listUserTest_Fail() {
        when(userDao.listUser())
        	.thenThrow(new RuntimeException("DB error"));
        
        UserResultDto result = userService.listUser();
        assertEquals("fail", result.getResult());
    }
    
    @Test
    void detailUserTest_Success() {
        UserDto user = UserDto.builder()
                .name("aaa")
                .email("aaa@aaa.com")
                .password("aaa")
                .build();
        
        when(userDao.detailUser(user.getId()))
        	.thenReturn(user);
        
        UserResultDto result = userService.detailUser(user.getId());
        assertEquals("success", result.getResult());
        assertEquals(result.getUserDto().getId(), user.getId());
        assertEquals(result.getUserDto().getName(), user.getName());
        assertEquals(result.getUserDto().getEmail(), user.getEmail());
    }
    
    @Test
    void detailUserTest_Fail() {
        when(userDao.detailUser(any(Long.class)))
        		.thenThrow(new RuntimeException("DB error"));
        
        UserResultDto result = userService.detailUser(any(Long.class));
        
        assertEquals("fail", result.getResult());
    }
    
    @Test
    void detailUserByEmailTest_Success() {
        UserDto user = UserDto.builder()
                .name("aaa")
                .email("aaa@aaa.com")
                .password("aaa")
                .build();
        
        when(userDao.detailUserByEmail(user.getEmail()))
        	.thenReturn(user);
        
        UserResultDto result = userService.detailUserByEmail(user.getEmail());
        assertEquals("success", result.getResult());
        assertEquals(result.getUserDto().getId(), user.getId());
        assertEquals(result.getUserDto().getName(), user.getName());
        assertEquals(result.getUserDto().getEmail(), user.getEmail());
    }
    
    @Test
    void detailUserByEmailTest_Fail() {
        when(userDao.detailUserByEmail(any(String.class)))
        		.thenThrow(new RuntimeException("DB error"));
        
        UserResultDto result = userService.detailUserByEmail(any(String.class));
        
        assertEquals("fail", result.getResult());
    }
}


