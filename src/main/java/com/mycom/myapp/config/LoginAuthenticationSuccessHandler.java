package com.mycom.myapp.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// Login 성공
@Component
public class LoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		
		LoginUserDetails userDetails = (LoginUserDetails) authentication.getPrincipal();
		
		Long userId = userDetails.getUserId();
		
		HttpSession session = request.getSession();
		
		session.setAttribute("userId", userDetails.getUserId());
		
		String jsonStr = String.format("""
					{
						"result":"success",
						"userId":"%s"
					}
					""", userId);
		
		response.getWriter().write(jsonStr);
	}

}
