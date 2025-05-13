package com.mycom.myapp.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class LoginUserDetailsTest {

    private static final String TEST_USERNAME = "test@example.com";
    private static final String TEST_PASSWORD = "password";
    private static final Long TEST_USER_ID = 123L;
    private Collection<GrantedAuthority> authorities;
    
    private LoginUserDetails userDetails;

    @BeforeEach
    void setUp() {
        authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        
        userDetails = LoginUserDetails.builder()
                .username(TEST_USERNAME)
                .password(TEST_PASSWORD)
                .userId(TEST_USER_ID)
                .authorities(authorities)
                .build();
    }

    @Test
    void getUsername_사용자이름_반환() {
        // when
        String username = userDetails.getUsername();
        
        // then
        assertEquals(TEST_USERNAME, username);
    }
    
    @Test
    void getPassword_비밀번호_반환() {
        // when
        String password = userDetails.getPassword();
        
        // then
        assertEquals(TEST_PASSWORD, password);
    }
    
    @Test
    void getUserId_사용자ID_반환() {
        // when
        Long userId = userDetails.getUserId();
        
        // then
        assertEquals(TEST_USER_ID, userId);
    }
    
    @Test
    void getAuthorities_권한목록_반환() {
        // when
        Collection<? extends GrantedAuthority> result = userDetails.getAuthorities();
        
        // then
        assertSame(authorities, result);
    }
}