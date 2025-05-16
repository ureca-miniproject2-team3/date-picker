package com.mycom.myapp.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.mycom.myapp.auth.dao.LoginDao;
import com.mycom.myapp.users.dto.UserDto;

public class LoginUserDetailsServiceTest {

    private LoginUserDetailsService userDetailsService;
    private LoginDao loginDao;
    
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password";
    private static final Long TEST_USER_ID = 123L;

    @BeforeEach
    void setUp() {
        loginDao = mock(LoginDao.class);
        userDetailsService = new LoginUserDetailsService(loginDao);
    }

    @Test
    void loadUserByUsername_사용자존재_UserDetails반환() {
        // given
        UserDto userDto = new UserDto();
        userDto.setId(TEST_USER_ID);
        userDto.setEmail(TEST_EMAIL);
        userDto.setPassword(TEST_PASSWORD);
        
        when(loginDao.findByEmail(TEST_EMAIL)).thenReturn(userDto);
        
        // when
        UserDetails userDetails = userDetailsService.loadUserByUsername(TEST_EMAIL);
        
        // then
        assertEquals(TEST_EMAIL, userDetails.getUsername());
        assertEquals(TEST_PASSWORD, userDetails.getPassword());
        assertEquals(TEST_USER_ID, ((LoginUserDetails)userDetails).getUserId());
    }
    
    @Test
    void loadUserByUsername_사용자없음_예외발생() {
        // given
        when(loginDao.findByEmail(TEST_EMAIL)).thenReturn(null);
        
        // when & then
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(TEST_EMAIL);
        });
    }
}