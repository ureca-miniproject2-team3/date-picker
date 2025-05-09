package com.mycom.myapp.users.service;

import com.mycom.myapp.users.dto.UserDto;
import com.mycom.myapp.users.dto.UserResultDto;

public interface UserService {

	public UserResultDto registerUser(UserDto userDto);
	
}
