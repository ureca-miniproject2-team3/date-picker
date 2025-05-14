package com.mycom.myapp.aspect;

import com.mycom.myapp.config.LoginUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

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
}
