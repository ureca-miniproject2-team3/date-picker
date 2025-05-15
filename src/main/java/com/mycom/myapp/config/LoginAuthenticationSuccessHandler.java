package com.mycom.myapp.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.mycom.myapp.notifications.service.AlertService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

// Login 성공
@Component
@Slf4j
public class LoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	@Autowired
	private AlertService alertService;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		
		LoginUserDetails userDetails = (LoginUserDetails) authentication.getPrincipal();
		
		Long userId = userDetails.getUserId();
		
		HttpSession session = request.getSession();
		
		session.setAttribute("userId", userDetails.getUserId());
		
		try {
			alertService.sendUnsentNotifications(userId);
		} catch (Exception e) {
			log.error("알림 전 중 오류 발생: {}", e.getMessage(), e);
		}
		
		String jsonStr = String.format("""
					{
						"result":"success",
						"userId":"%s"
					}
					""", userId);
		
		response.getWriter().write(jsonStr);
	}

}
