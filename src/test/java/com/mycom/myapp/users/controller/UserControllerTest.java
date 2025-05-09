package com.mycom.myapp.users.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerUser_success() throws Exception {
        UserDto userDto = UserDto.builder().
        				id(1).
        				name("aaa").
        				email("aaa@aaa.com").
        				password("aaa").
        				build();
        
        UserResultDto resultDto = new UserResultDto();
        resultDto.setResult("success");

        when(userService.registerUser(any(UserDto.class))).thenReturn(resultDto);

        mockMvc.perform(post("/api/users")
        		 .with(csrf()) 
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"));
    }

    @Test
    void registerUser_exist() throws Exception {
        UserDto userDto = UserDto.builder().
				id(1).
				name("aaa").
				email("aaa@aaa.com").
				password("aaa").
				build();
        
        UserResultDto resultDto = new UserResultDto();
        resultDto.setResult("exist");

        when(userService.registerUser(any(UserDto.class))).thenReturn(resultDto);

        mockMvc.perform(post("/api/users")
        		 .with(csrf()) 
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value("exist"));
    }

    @Test
    void registerUser_fail() throws Exception {
        UserDto userDto = UserDto.builder().
				id(1).
				name("aaa").
				email("aaa@aaa.com").
				password("aaa").
				build();
        
        UserResultDto resultDto = new UserResultDto();
        resultDto.setResult("fail");

        when(userService.registerUser(any(UserDto.class))).thenReturn(resultDto);

        mockMvc.perform(post("/api/users")
        		.with(csrf()) 
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.result").value("fail"));
    }
}