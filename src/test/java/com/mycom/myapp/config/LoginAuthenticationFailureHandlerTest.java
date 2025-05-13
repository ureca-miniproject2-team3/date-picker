package com.mycom.myapp.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoginAuthenticationFailureHandlerTest {

    private LoginAuthenticationFailureHandler handler;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private AuthenticationException exception;
    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() throws Exception {
        handler = new LoginAuthenticationFailureHandler();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        exception = mock(BadCredentialsException.class);
        
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    void onAuthenticationFailure_응답상태_401_설정() throws Exception {
        // when
        handler.onAuthenticationFailure(request, response, exception);
        
        // then
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
    
    @Test
    void onAuthenticationFailure_ContentType_JSON_설정() throws Exception {
        // when
        handler.onAuthenticationFailure(request, response, exception);
        
        // then
        verify(response).setContentType("application/json");
    }
    
    @Test
    void onAuthenticationFailure_실패_JSON_응답() throws Exception {
        // when
        handler.onAuthenticationFailure(request, response, exception);
        printWriter.flush();
        
        // then
        String expectedJson = "{\"result\":\"fail\"}";
        assertEquals(expectedJson.trim(), stringWriter.toString().trim());
    }
}