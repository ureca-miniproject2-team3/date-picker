package com.mycom.myapp.config;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
public class LoginUserDetails implements UserDetails {

	private static final long serialVersionUID = 1L;
	
	private final String username;

	private final String password;
	
	private final Collection<? extends GrantedAuthority> authorities;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() { return password; }

	@Override
	public String getUsername() { return username; }

}
