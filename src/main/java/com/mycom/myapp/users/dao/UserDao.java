package com.mycom.myapp.users.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mycom.myapp.users.dto.UserDto;

@Mapper
public interface UserDao {

	int registerUser(UserDto userDto);
	
	List<UserDto> listUser();
	
	UserDto detailUser(Long userId);
	
	UserDto detailUserByEmail(String email);
	
}
