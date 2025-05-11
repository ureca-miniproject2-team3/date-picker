package com.mycom.myapp.users.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycom.myapp.users.dto.UserDto;
import com.mycom.myapp.users.dto.UserResultDto;
import com.mycom.myapp.users.service.UserService;

@WithMockUser
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void registerUser_success() throws Exception {
        UserResultDto resultDto = new UserResultDto();
        resultDto.setResult("success");

        when(userService.registerUser(any(UserDto.class))).thenReturn(resultDto);

        mockMvc.perform(post("/api/users")
        		 .with(csrf())
        		 .param("id", "1")
        		 .param("name", "aaa")
        		 .param("email", "aaa@aaa.com")
        		 .param("password", "aaa"))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$.result").value("success"));
    }

    @Test
    void registerUser_exist() throws Exception {
        UserResultDto resultDto = new UserResultDto();
        resultDto.setResult("exist");

        when(userService.registerUser(any(UserDto.class))).thenReturn(resultDto);

        mockMvc.perform(post("/api/users")
        		 .with(csrf()) 
        		 .param("id", "1")
        		 .param("name", "aaa")
        		 .param("email", "aaa@aaa.com")
        		 .param("password", "aaa"))
                 .andExpect(status().isBadRequest())
                 .andExpect(jsonPath("$.result").value("exist"));
    }
    
    @Test
    void registerUser_fail() throws Exception {
        UserResultDto resultDto = new UserResultDto();
        resultDto.setResult("fail");

        when(userService.registerUser(any(UserDto.class))).thenReturn(resultDto);

        mockMvc.perform(post("/api/users")
        		.with(csrf()) 
        		.param("id", "1")
       			.param("name", "aaa")
       		 	.param("email", "aaa@aaa.com")
       		 	.param("password", "aaa"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.result").value("fail"));
    }

    @Test
    void listUser_fail() throws Exception {
        UserResultDto resultDto = new UserResultDto();
        resultDto.setResult("fail");

        when(userService.listUser()).thenReturn(resultDto);

        mockMvc.perform(get("/api/users")
        		.with(csrf())) 
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.result").value("fail"));
    }
    
    @Test
    void listUser_success() throws Exception {
        UserDto user1 = UserDto.builder().
        				id(1L).
        				name("aaa").
        				email("aaa@aaa.com").
        				password("aaa").
        				build();
        
        UserDto user2 = UserDto.builder().
        		id(2L).
        		name("bbb").
        		email("bbb@bbb.com").
        		password("bbb").
        		build();
        
        UserResultDto resultDto = new UserResultDto();
        resultDto.setResult("success");
        resultDto.setUserDtoList(List.of(user1,user2));
        
        when(userService.listUser()).thenReturn(resultDto);

        mockMvc.perform(get("/api/users")
        		 .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"));
    }
    
    @Test
    void detailUser_fail() throws Exception {
    	Long userId = Long.MAX_VALUE;
        UserResultDto resultDto = new UserResultDto();
        resultDto.setResult("fail");

        when(userService.detailUser(userId)).thenReturn(resultDto);

        mockMvc.perform(get("/api/users/" + userId)
        		.with(csrf()) 
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.result").value("fail"));
    }
    
    @Test
    void detailUser_success() throws Exception {
        UserDto userDto = UserDto.builder().
        				id(1L).
        				name("aaa").
        				email("aaa@aaa.com").
        				password("aaa").
        				build();
        
        UserResultDto resultDto = new UserResultDto();
        resultDto.setResult("success");
        resultDto.setUserDto(userDto);
        
        when(userService.detailUser(1L)).thenReturn(resultDto);

        mockMvc.perform(get("/api/users/1")
        		 .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"));
    }
    
    @Test
    void detailUserByEmail_fail() throws Exception {
    	String email = "aaa@aaa.com";
        UserResultDto resultDto = new UserResultDto();
        resultDto.setResult("fail");

        when(userService.detailUserByEmail(email)).thenReturn(resultDto);

        mockMvc.perform(get("/api/users/search")
        		.param("email", email)
        		.with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.result").value("fail"));
    }
    
    @Test
    void detailUserByEmail_success() throws Exception {
    	String email = "aaa@aaa.com";
        UserDto userDto = UserDto.builder().
        				id(1L).
        				name("aaa").
        				email(email).
        				password("aaa").
        				build();
        
        UserResultDto resultDto = new UserResultDto();
        resultDto.setResult("success");
        resultDto.setUserDto(userDto);
        
        when(userService.detailUserByEmail(email)).thenReturn(resultDto);

        mockMvc.perform(get("/api/users/search")
        		.param("email", email)
        		.with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"));
    }
}