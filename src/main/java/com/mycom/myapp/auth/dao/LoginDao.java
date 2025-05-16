package com.mycom.myapp.auth.dao;

import org.apache.ibatis.annotations.Mapper;

import com.mycom.myapp.users.dto.UserDto;

@Mapper
public interface LoginDao {

	UserDto findByEmail(String email);
}
