package com.mycom.myapp.users.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
	public void registerUserTest() {
		UserDto userDto = UserDto.builder()
						.name("aaa")
						.email("aaa@aaa.com")
						.password("aaa")
						.build();
		
		int ret = userDao.registerUser(userDto);
		
		assertEquals(1, ret);
	}
}
