package com.mycom.myapp.aspect;

import com.mycom.myapp.config.LoginUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    // Pointcut
    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    private void getPointcut() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PutMapping)")
    private void putPointcut() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    private void postPointcut() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    private void deletePointcut() {
    }

    @Pointcut("execution(* com.mycom.myapp.config.LoginAuthenticationSuccessHandler.onAuthenticationSuccess(..))")
    private void loginPointcut() {
    }

    @Pointcut("execution(* org.springframework.security.web.authentication.logout.LogoutFilter.doFilter(..))")
    private void logoutPointcut() {
    }

    // Join Point
    @After("getPointcut()")
    public void logGetMapping(JoinPoint joinPoint) {
        logHttpMethod(joinPoint, "GET");
    }

    @After("putPointcut()")
    public void logPutMapping(JoinPoint joinPoint) {
        logHttpMethod(joinPoint, "PUT");
    }

    @After("postPointcut()")
    public void logPostMapping(JoinPoint joinPoint) {
        logHttpMethod(joinPoint, "POST");
    }

    @After("deletePointcut()")
    public void logDeleteMapping(JoinPoint joinPoint) {
        logHttpMethod(joinPoint, "DELETE");
    }

    @After("loginPointcut()")
    public void logLoginSuccess(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length >= 3 && args[2] instanceof Authentication) {
            Authentication authentication = (Authentication) args[2];
            logLoginEvent(authentication, "로그인 성공");
        }
    }

    @After("logoutPointcut()")
    public void logLogout(JoinPoint joinPoint) {
        // Get the current authentication before it's cleared by the logout process
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            String username;
            Long userId = null;

            Object principal = auth.getPrincipal();

            if (principal instanceof LoginUserDetails user) {
                username = user.getUsername();
                userId = user.getUserId();
            } else {
                username = auth.getName();
            }

            log.info("[인증] 사용자 [ id={} / {} ] 이 [ 로그아웃 ] 했습니다.", userId, username);
        }
    }

    private void logHttpMethod(JoinPoint joinPoint, String httpMethod) {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return;
        }

        String username;
        Long userId = null;

        Object principal = auth.getPrincipal();

        if (principal instanceof LoginUserDetails user) {
            username = user.getUsername();
            userId = user.getUserId();
        } else {
            username = auth.getName();
        }

        String methodName = joinPoint.getSignature().getName();

        log.info("[{}] 사용자 [ id={} / {} ] 이 [ {} ] 을 실행했습니다.", httpMethod, userId, username, methodName);
    }

    private void logLoginEvent(Authentication auth, String eventType) {
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return;
        }

        String username;
        Long userId = null;

        Object principal = auth.getPrincipal();

        if (principal instanceof LoginUserDetails user) {
            username = user.getUsername();
            userId = user.getUserId();
        } else {
            username = auth.getName();
        }

        log.info("[인증] 사용자 [ id={} / {} ] 이 [ {} ] 했습니다.", userId, username, eventType);
    }
}
