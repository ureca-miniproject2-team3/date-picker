package com.mycom.myapp.aspect;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.mycom.myapp.config.LoginUserDetails;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.lang.reflect.Method;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class LoggingAspectTest {

    @InjectMocks
    private LoggingAspect loggingAspect;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private Signature signature;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private LoginUserDetails userDetails;

    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setup() {
        // Set up logger to capture log messages
        Logger logger = (Logger) LoggerFactory.getLogger(LoggingAspect.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        // Set up JoinPoint mock
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("testMethod");
    }

    @Test
    void logGetMapping_authenticatedUser_shouldLogCorrectly() {
        // Given
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(userDetails.getUsername()).thenReturn("testUser");
            when(userDetails.getUserId()).thenReturn(1L);

            // When
            loggingAspect.logGetMapping(joinPoint);

            // Then
            List<ILoggingEvent> logsList = listAppender.list;
            assert(!logsList.isEmpty());
            ILoggingEvent lastLog = logsList.get(logsList.size() - 1);
            assert(lastLog.getLevel().equals(Level.INFO));
            assert(lastLog.getFormattedMessage().contains("[GET] 사용자 [ id=1 / testUser ] 이 [ testMethod ] 을 실행했습니다."));
        }
    }

    @Test
    void logGetMapping_anonymousUser_shouldNotLog() {
        // Given
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(mock(AnonymousAuthenticationToken.class));

            // When
            loggingAspect.logGetMapping(joinPoint);

            // Then
            List<ILoggingEvent> logsList = listAppender.list;
            assert(logsList.isEmpty());
        }
    }

    @Test
    void logPutMapping_authenticatedUser_shouldLogCorrectly() {
        // Given
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(userDetails.getUsername()).thenReturn("testUser");
            when(userDetails.getUserId()).thenReturn(1L);

            // When
            loggingAspect.logPutMapping(joinPoint);

            // Then
            List<ILoggingEvent> logsList = listAppender.list;
            assert(!logsList.isEmpty());
            ILoggingEvent lastLog = logsList.get(logsList.size() - 1);
            assert(lastLog.getLevel().equals(Level.INFO));
            assert(lastLog.getFormattedMessage().contains("[PUT] 사용자 [ id=1 / testUser ] 이 [ testMethod ] 을 실행했습니다."));
        }
    }

    @Test
    void logPostMapping_authenticatedUser_shouldLogCorrectly() {
        // Given
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(userDetails.getUsername()).thenReturn("testUser");
            when(userDetails.getUserId()).thenReturn(1L);

            // When
            loggingAspect.logPostMapping(joinPoint);

            // Then
            List<ILoggingEvent> logsList = listAppender.list;
            assert(!logsList.isEmpty());
            ILoggingEvent lastLog = logsList.get(logsList.size() - 1);
            assert(lastLog.getLevel().equals(Level.INFO));
            assert(lastLog.getFormattedMessage().contains("[POST] 사용자 [ id=1 / testUser ] 이 [ testMethod ] 을 실행했습니다."));
        }
    }

    @Test
    void logDeleteMapping_authenticatedUser_shouldLogCorrectly() {
        // Given
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(userDetails.getUsername()).thenReturn("testUser");
            when(userDetails.getUserId()).thenReturn(1L);

            // When
            loggingAspect.logDeleteMapping(joinPoint);

            // Then
            List<ILoggingEvent> logsList = listAppender.list;
            assert(!logsList.isEmpty());
            ILoggingEvent lastLog = logsList.get(logsList.size() - 1);
            assert(lastLog.getLevel().equals(Level.INFO));
            assert(lastLog.getFormattedMessage().contains("[DELETE] 사용자 [ id=1 / testUser ] 이 [ testMethod ] 을 실행했습니다."));
        }
    }

    @Test
    void logLoginSuccess_shouldLogCorrectly() {
        // Given
        Authentication authArg = mock(Authentication.class);
        when(authArg.isAuthenticated()).thenReturn(true);
        when(authArg.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testUser");
        when(userDetails.getUserId()).thenReturn(1L);

        Object[] args = new Object[3];
        args[2] = authArg;
        when(joinPoint.getArgs()).thenReturn(args);

        // When
        loggingAspect.logLoginSuccess(joinPoint);

        // Then
        List<ILoggingEvent> logsList = listAppender.list;
        assert(!logsList.isEmpty());
        ILoggingEvent lastLog = logsList.get(logsList.size() - 1);
        assert(lastLog.getLevel().equals(Level.INFO));
        assert(lastLog.getFormattedMessage().contains("[인증] 사용자 [ id=1 / testUser ] 이 [ 로그인 성공 ] 했습니다."));
    }

    @Test
    void logLogout_authenticatedUser_shouldLogCorrectly() {
        // Given
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(userDetails.getUsername()).thenReturn("testUser");
            when(userDetails.getUserId()).thenReturn(1L);

            // When
            loggingAspect.logLogout(joinPoint);

            // Then
            List<ILoggingEvent> logsList = listAppender.list;
            assert(!logsList.isEmpty());
            ILoggingEvent lastLog = logsList.get(logsList.size() - 1);
            assert(lastLog.getLevel().equals(Level.INFO));
            assert(lastLog.getFormattedMessage().contains("[인증] 사용자 [ id=1 / testUser ] 이 [ 로그아웃 ] 했습니다."));
        }
    }

    @Test
    void logLogout_anonymousUser_shouldNotLog() {
        // Given
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(mock(AnonymousAuthenticationToken.class));

            // When
            loggingAspect.logLogout(joinPoint);

            // Then
            List<ILoggingEvent> logsList = listAppender.list;
            assert(logsList.isEmpty());
        }
    }

    @Test
    void logLogout_notAnonymousAuthenticationToken_shouldLog() {
        // Given
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(userDetails.getUsername()).thenReturn("testUser");
            when(userDetails.getUserId()).thenReturn(1L);

            // Explicitly verify it's not an AnonymousAuthenticationToken
            assert(!(authentication instanceof AnonymousAuthenticationToken));

            // When
            loggingAspect.logLogout(joinPoint);

            // Then
            List<ILoggingEvent> logsList = listAppender.list;
            assert(!logsList.isEmpty());
            ILoggingEvent lastLog = logsList.get(logsList.size() - 1);
            assert(lastLog.getLevel().equals(Level.INFO));
            assert(lastLog.getFormattedMessage().contains("[인증] 사용자 [ id=1 / testUser ] 이 [ 로그아웃 ] 했습니다."));
        }
    }

    @Test
    void extractUserInfo_regularAuthentication_shouldExtractOnlyUsername() {
        // Given
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // Create an authenticated TestingAuthenticationToken
            TestingAuthenticationToken regularAuth = new TestingAuthenticationToken("testUser", "password");
            regularAuth.setAuthenticated(true);

            when(securityContext.getAuthentication()).thenReturn(regularAuth);

            // When
            loggingAspect.logGetMapping(joinPoint);

            // Then
            List<ILoggingEvent> logsList = listAppender.list;
            assert(!logsList.isEmpty());
            ILoggingEvent lastLog = logsList.get(logsList.size() - 1);
            assert(lastLog.getLevel().equals(Level.INFO));

            // Check that the log contains the username but doesn't verify the exact format of the null userId
            assert(lastLog.getFormattedMessage().contains("[GET]"));
            assert(lastLog.getFormattedMessage().contains("testUser"));
            assert(lastLog.getFormattedMessage().contains("testMethod"));
        }
    }
    @Test
    void logPutMapping_anonymousUser_shouldNotLog() {
        // Given
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(mock(AnonymousAuthenticationToken.class));

            // When
            loggingAspect.logPutMapping(joinPoint);

            // Then
            List<ILoggingEvent> logsList = listAppender.list;
            assert(logsList.isEmpty());
        }
    }

    @Test
    void logPostMapping_anonymousUser_shouldNotLog() {
        // Given
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(mock(AnonymousAuthenticationToken.class));

            // When
            loggingAspect.logPostMapping(joinPoint);

            // Then
            List<ILoggingEvent> logsList = listAppender.list;
            assert(logsList.isEmpty());
        }
    }

    @Test
    void logDeleteMapping_anonymousUser_shouldNotLog() {
        // Given
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(mock(AnonymousAuthenticationToken.class));

            // When
            loggingAspect.logDeleteMapping(joinPoint);

            // Then
            List<ILoggingEvent> logsList = listAppender.list;
            assert(logsList.isEmpty());
        }
    }

    @Test
    void logLoginSuccess_insufficientArguments_shouldNotLog() {
        // Given
        Object[] args = new Object[2]; // Less than 3 arguments
        when(joinPoint.getArgs()).thenReturn(args);

        // When
        loggingAspect.logLoginSuccess(joinPoint);

        // Then
        List<ILoggingEvent> logsList = listAppender.list;
        assert(logsList.isEmpty());
    }

    @Test
    void logLoginSuccess_anonymousUser_shouldNotLog() {
        // Given
        AnonymousAuthenticationToken anonymousAuth = mock(AnonymousAuthenticationToken.class);
        when(anonymousAuth.isAuthenticated()).thenReturn(true);

        Object[] args = new Object[3];
        args[2] = anonymousAuth;
        when(joinPoint.getArgs()).thenReturn(args);

        // When
        loggingAspect.logLoginSuccess(joinPoint);

        // Then
        List<ILoggingEvent> logsList = listAppender.list;
        assert(logsList.isEmpty());
    }

    @Test
    void logHttpMethod_noAuthentication_shouldNotLog() {
        // Given
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(null);

            // When - Using logGetMapping which calls logHttpMethod
            loggingAspect.logGetMapping(joinPoint);

            // Then
            List<ILoggingEvent> logsList = listAppender.list;
            assert(logsList.isEmpty());
        }
    }

    @Test
    void logHttpMethod_anonymousAuthenticationToken_shouldNotLog() {
        // Given
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // Create an AnonymousAuthenticationToken
            AnonymousAuthenticationToken anonymousAuth = mock(AnonymousAuthenticationToken.class);
            when(anonymousAuth.isAuthenticated()).thenReturn(true);
            when(securityContext.getAuthentication()).thenReturn(anonymousAuth);

            // Explicitly verify it's an AnonymousAuthenticationToken
            assert(anonymousAuth instanceof AnonymousAuthenticationToken);

            // When - Using logGetMapping which calls logHttpMethod
            loggingAspect.logGetMapping(joinPoint);

            // Then
            List<ILoggingEvent> logsList = listAppender.list;
            assert(logsList.isEmpty());
        }
    }

    @Test
    void logHttpMethod_notAuthenticated_shouldNotLog() {
        // Given
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(false);

            // When - Using logGetMapping which calls logHttpMethod
            loggingAspect.logGetMapping(joinPoint);

            // Then
            List<ILoggingEvent> logsList = listAppender.list;
            assert(logsList.isEmpty());
        }
    }

    @Test
    void logLoginEvent_noAuthentication_shouldNotLog() {
        // Given
        Authentication nullAuth = null;
        Object[] args = new Object[3];
        args[2] = nullAuth;
        when(joinPoint.getArgs()).thenReturn(args);

        // When
        loggingAspect.logLoginSuccess(joinPoint);

        // Then
        List<ILoggingEvent> logsList = listAppender.list;
        assert(logsList.isEmpty());
    }

    @Test
    void logLoginEvent_notAuthenticated_shouldNotLog() {
        // Given
        Authentication notAuthenticatedAuth = mock(Authentication.class);
        when(notAuthenticatedAuth.isAuthenticated()).thenReturn(false);

        Object[] args = new Object[3];
        args[2] = notAuthenticatedAuth;
        when(joinPoint.getArgs()).thenReturn(args);

        // When
        loggingAspect.logLoginSuccess(joinPoint);

        // Then
        List<ILoggingEvent> logsList = listAppender.list;
        assert(logsList.isEmpty());
    }

    @Test
    void logLoginEvent_directCall_shouldLogCorrectly() {
        // Given
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testUser");
        when(userDetails.getUserId()).thenReturn(1L);

        // Use reflection to access the private method
        java.lang.reflect.Method method;
        try {
            method = LoggingAspect.class.getDeclaredMethod("logLoginEvent", Authentication.class, String.class);
            method.setAccessible(true);

            // When
            method.invoke(loggingAspect, auth, "테스트 이벤트");

            // Then
            List<ILoggingEvent> logsList = listAppender.list;
            assert(!logsList.isEmpty());
            ILoggingEvent lastLog = logsList.get(logsList.size() - 1);
            assert(lastLog.getLevel().equals(Level.INFO));
            assert(lastLog.getFormattedMessage().contains("[인증] 사용자 [ id=1 / testUser ] 이 [ 테스트 이벤트 ] 했습니다."));
        } catch (Exception e) {
            assert(false); // Fail the test if reflection fails
        }
    }

    @Test
    void logLoginEvent_directCall_anonymousUser_shouldNotLog() {
        // Given
        AnonymousAuthenticationToken anonymousAuth = mock(AnonymousAuthenticationToken.class);
        when(anonymousAuth.isAuthenticated()).thenReturn(true);

        // Use reflection to access the private method
        java.lang.reflect.Method method;
        try {
            method = LoggingAspect.class.getDeclaredMethod("logLoginEvent", Authentication.class, String.class);
            method.setAccessible(true);

            // When
            method.invoke(loggingAspect, anonymousAuth, "테스트 이벤트");

            // Then
            List<ILoggingEvent> logsList = listAppender.list;
            assert(logsList.isEmpty());
        } catch (Exception e) {
            assert(false); // Fail the test if reflection fails
        }
    }

    @Test
    void logLoginEvent_directCall_nullAuth_shouldNotLog() {
        // Given
        Authentication nullAuth = null;

        // Use reflection to access the private method
        java.lang.reflect.Method method;
        try {
            method = LoggingAspect.class.getDeclaredMethod("logLoginEvent", Authentication.class, String.class);
            method.setAccessible(true);

            // When
            method.invoke(loggingAspect, nullAuth, "테스트 이벤트");

            // Then
            List<ILoggingEvent> logsList = listAppender.list;
            assert(logsList.isEmpty());
        } catch (Exception e) {
            assert(false); // Fail the test if reflection fails
        }
    }

    @Test
    void extractUserInfo_nonLoginUserDetails_shouldExtractOnlyUsername() {
        // Given
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn("testUser"); // String principal instead of LoginUserDetails
        when(auth.getName()).thenReturn("testUser");

        // Use reflection to access the private method
        java.lang.reflect.Method method;
        try {
            method = LoggingAspect.class.getDeclaredMethod("extractUserInfo", Authentication.class);
            method.setAccessible(true);

            // When
            Object result = method.invoke(loggingAspect, auth);

            // Then
            // Verify the result is a UserInfo with username="testUser" and userId=null
            java.lang.reflect.Field usernameField = result.getClass().getDeclaredField("username");
            java.lang.reflect.Field userIdField = result.getClass().getDeclaredField("userId");
            usernameField.setAccessible(true);
            userIdField.setAccessible(true);

            assert("testUser".equals(usernameField.get(result)));
            assert(null == userIdField.get(result));
        } catch (Exception e) {
            assert(false); // Fail the test if reflection fails
        }
    }

    @Test
    void logLogout_nullAuthentication_shouldNotLog() {
        // Given
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(null);

            // When
            loggingAspect.logLogout(joinPoint);

            // Then
            List<ILoggingEvent> logsList = listAppender.list;
            assert(logsList.isEmpty());
        }
    }

    @Test
    void logLogout_notAuthenticated_shouldNotLog() {
        // Given
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(false);

            // When
            loggingAspect.logLogout(joinPoint);

            // Then
            List<ILoggingEvent> logsList = listAppender.list;
            assert(logsList.isEmpty());
        }
    }

    @Test
    void logLogout_isAnonymousAuthenticationToken_shouldNotLog() {
        // Given
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // Create an authenticated AnonymousAuthenticationToken
            AnonymousAuthenticationToken anonymousAuth = mock(AnonymousAuthenticationToken.class);
            when(anonymousAuth.isAuthenticated()).thenReturn(true);
            when(securityContext.getAuthentication()).thenReturn(anonymousAuth);

            // Explicitly verify it's an AnonymousAuthenticationToken
            assert(anonymousAuth instanceof AnonymousAuthenticationToken);

            // When
            loggingAspect.logLogout(joinPoint);

            // Then
            List<ILoggingEvent> logsList = listAppender.list;
            assert(logsList.isEmpty());
        }
    }

    @Test
    void invokeAllPointcutMethods() throws Exception {
        Method[] methods = LoggingAspect.class.getDeclaredMethods();
        for (Method m : methods) {
            if (m.isAnnotationPresent(Pointcut.class)) {
                m.setAccessible(true);
                // 실제 로직이 없으니 예외만 안 터지면 OK
                assertDoesNotThrow(() -> m.invoke(loggingAspect),
                        () -> "@Pointcut 메서드 " + m.getName() + " 호출 실패");
            }
        }
    }
}
