package com.mycom.myapp.users.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.mycom.myapp.users.dto.UserDto;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserDaoTest {
	
	@Autowired
	private UserDao userDao;
	
	@Test
	void registerUserTest() {
		UserDto userDto = UserDto.builder()
						.name("aaa")
						.email("aaa@aaa.com")
						.password("aaa")
						.build();
		
		int ret = userDao.registerUser(userDto);
		
		assertEquals(1, ret);
	}
	
	@Test
	void listUserTest() {
		UserDto userDto = UserDto.builder()
				.name("aaa")
				.email("aaa@aaa.com")
				.password("aaa")
				.build();
		
		userDao.registerUser(userDto);
		
		List<UserDto> userDtoList = userDao.listUser();
		
		assertNotNull(userDtoList);
	}
	
	@Test
	void detailUserTest() {
		UserDto userDto = UserDto.builder()
				.name("aaa")
				.email("aaa@aaa.com")
				.password("aaa")
				.build();
		
		userDao.registerUser(userDto);
		
		UserDto detailUser = userDao.detailUser(userDto.getId());
		
		assertEquals(detailUser.getId(), userDto.getId());
		assertEquals(detailUser.getName(), "aaa");
		assertEquals(detailUser.getEmail(), "aaa@aaa.com");
	}
	
	@Test
	void detailUserByEmailTest() {
		UserDto userDto = UserDto.builder()
				.name("aaa")
				.email("aaa@aaa.com")
				.password("aaa")
				.build();
		
		userDao.registerUser(userDto);
		
		UserDto detailUser = userDao.detailUserByEmail(userDto.getEmail());
		
		assertEquals(detailUser.getId(), userDto.getId());
		assertEquals(detailUser.getName(), "aaa");
		assertEquals(detailUser.getEmail(), "aaa@aaa.com");
	}
	
	
}
