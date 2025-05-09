package com.mycom.myapp.config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mycom.myapp.auth.dao.LoginDao;
import com.mycom.myapp.users.dto.UserDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginUserDetailsService implements UserDetailsService {
	
	private final LoginDao loginDao;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserDto userDto = loginDao.findByEmail(email);
		
		if(userDto != null) {
			UserDetails userDetails = LoginUserDetails.builder()
							.username(userDto.getEmail())
							.password(userDto.getPassword())
							.build();
			
			return userDetails;
		}
		
		throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
	}

}
