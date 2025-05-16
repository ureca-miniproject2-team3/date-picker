package com.mycom.myapp.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

public class SecurityConfigTest {

    private SecurityConfig securityConfig;
    private LoginAuthenticationSuccessHandler successHandler;
    private LoginAuthenticationFailureHandler failureHandler;
    private HttpSecurity httpSecurity;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig();
        successHandler = mock(LoginAuthenticationSuccessHandler.class);
        failureHandler = mock(LoginAuthenticationFailureHandler.class);
        httpSecurity = mock(HttpSecurity.class);
    }

    @Test
    void passwordEncoder_BCryptPasswordEncoder_반환() {
        // when
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        
        // then
        assertNotNull(passwordEncoder);
        assertTrue(passwordEncoder instanceof BCryptPasswordEncoder);
    }
}