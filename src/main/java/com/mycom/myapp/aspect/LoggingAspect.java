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

    @Pointcut("execution(* com.mycom.myapp.config.LoginAuthenticationSuccessHandler.onAuthenticationSuccess(..))")
    private void loginPointcut() {
    }

    @Pointcut("execution(* org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler.logout(..))")
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
        if (args.length >= 3 && args[2] instanceof Authentication authentication) {
            logLoginEvent(authentication, "로그인 성공");
        }
    }

    @After("logoutPointcut()")
    public void logLogout(JoinPoint joinPoint) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            UserInfo userInfo = extractUserInfo(auth);
            log.info("[인증] 사용자 [ id={} / {} ] 이 [ 로그아웃 ] 했습니다.", userInfo.userId(), userInfo.username());
        }
    }

    private void logHttpMethod(JoinPoint joinPoint, String httpMethod) {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return;
        }

        UserInfo userInfo = extractUserInfo(auth);
        String methodName = joinPoint.getSignature().getName();

        log.info("[{}] 사용자 [ id={} / {} ] 이 [ {} ] 을 실행했습니다.", 
                httpMethod, userInfo.userId(), userInfo.username(), methodName);
    }

    private void logLoginEvent(Authentication auth, String eventType) {
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return;
        }

        UserInfo userInfo = extractUserInfo(auth);
        log.info("[인증] 사용자 [ id={} / {} ] 이 [ {} ] 했습니다.", 
                userInfo.userId(), userInfo.username(), eventType);
    }

    private record UserInfo(String username, Long userId) {}

    private UserInfo extractUserInfo(Authentication auth) {
        String username;
        Long userId = null;

        Object principal = auth.getPrincipal();

        if (principal instanceof LoginUserDetails user) {
            username = user.getUsername();
            userId = user.getUserId();
        } else {
            username = auth.getName();
        }

        return new UserInfo(username, userId);
    }
}
