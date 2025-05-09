package com.mycom.myapp.users.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mycom.myapp.auth.dao.LoginDao;
import com.mycom.myapp.users.dao.UserDao;
import com.mycom.myapp.users.dto.UserDto;
import com.mycom.myapp.users.dto.UserResultDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserDao userDao;
	private final LoginDao loginDao;
	private final PasswordEncoder passwordEncoder; 
	
	@Override
	public UserResultDto registerUser(UserDto userDto) {
		UserResultDto userResultDto = new UserResultDto();
		
		try {
			UserDto findDto = loginDao.findByEmail(userDto.getEmail());
			
			// 이미 계정이 존재하는 경우
			if(findDto != null) {
				userResultDto.setResult("exist");
				return userResultDto;
			}
			
			// 패스워드 암호화
			String encodedPassword = passwordEncoder.encode(userDto.getPassword());
			
			userDto.setPassword(encodedPassword);
			
			userDao.registerUser(userDto);
			
			userResultDto.setResult("success");
			
		} catch (Exception e) {
			e.printStackTrace();
			userResultDto.setResult("fail");
		}
		
		return userResultDto;
	}
	
	
}
