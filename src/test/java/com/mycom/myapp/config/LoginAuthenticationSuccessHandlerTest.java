package com.mycom.myapp.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import com.mycom.myapp.notifications.service.AlertService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class LoginAuthenticationSuccessHandlerTest {

    private LoginAuthenticationSuccessHandler handler;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private Authentication authentication;
    private LoginUserDetails userDetails;
    private HttpSession session;
    private AlertService alertService;
    
    private StringWriter stringWriter;
    private PrintWriter printWriter;

    private final Long TEST_USER_ID = 123L;

    @BeforeEach
    void setUp() throws Exception {
        handler = new LoginAuthenticationSuccessHandler();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        authentication = mock(Authentication.class);
        userDetails = mock(LoginUserDetails.class);
        session = mock(HttpSession.class);
        alertService = mock(AlertService.class);
        
        // Autowired 필드 설정
        ReflectionTestUtils.setField(handler, "alertService", alertService);
        
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUserId()).thenReturn(TEST_USER_ID);
        when(request.getSession()).thenReturn(session);
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    void onAuthenticationSuccess_응답상태_200_설정() throws Exception {
        // when
        handler.onAuthenticationSuccess(request, response, authentication);

        // then
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void onAuthenticationSuccess_ContentType_JSON_설정() throws Exception {
        // when
        handler.onAuthenticationSuccess(request, response, authentication);

        // then
        verify(response).setContentType("application/json");
    }

    @Test
    void onAuthenticationSuccess_세션에_userId_설정() throws Exception {
        // when
        handler.onAuthenticationSuccess(request, response, authentication);

        // then
        verify(session).setAttribute("userId", TEST_USER_ID);
    }

    @Test
    void onAuthenticationSuccess_성공_JSON_응답() throws Exception {
        // when
        handler.onAuthenticationSuccess(request, response, authentication);
        printWriter.flush();

        // then
        // Normalize whitespace for comparison
        String expectedJson = String.format("""
                {
                    "result":"success",
                    "userId":"%s"
                }
                """, TEST_USER_ID).trim().replaceAll("\\s+", "");
        String actualJson = stringWriter.toString().trim().replaceAll("\\s+", "");
        assertEquals(expectedJson, actualJson);
    }
    
    @Test
    void onAuthenticationSuccess_알림서비스_호출() throws Exception {
        // when
        handler.onAuthenticationSuccess(request, response, authentication);
        
        // then
        verify(alertService).sendUnsentNotifications(TEST_USER_ID);
    }
    
    @Test
    void onAuthenticationSuccess_알림서비스_예외발생시_로그인처리_정상완료() throws Exception {
        // given
        doThrow(new RuntimeException("알림 서비스 오류")).when(alertService).sendUnsentNotifications(any());
        
        // when
        handler.onAuthenticationSuccess(request, response, authentication);
        printWriter.flush();
        
        // then
        verify(response).setStatus(HttpServletResponse.SC_OK);
        String expectedJson = String.format("""
                {
                    "result":"success",
                    "userId":"%s"
                }
                """, TEST_USER_ID).trim().replaceAll("\\s+", "");
        String actualJson = stringWriter.toString().trim().replaceAll("\\s+", "");
        assertEquals(expectedJson, actualJson);
    }
}
