package com.mycom.myapp.users.dao;

import org.apache.ibatis.annotations.Mapper;

import com.mycom.myapp.users.dto.UserDto;

@Mapper
public interface UserDao {

	int registerUser(UserDto userDto);
	
}
