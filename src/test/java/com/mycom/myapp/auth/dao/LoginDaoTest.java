package com.mycom.myapp.auth.dao;

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
public class LoginDaoTest {

	@Autowired
	private LoginDao loginDao;
	
	@Test
	void findByEmailTest() {
		UserDto userDto = loginDao.findByEmail("hong@gildong.com");
		
		assertEquals(1, userDto.getId());
		assertEquals("hong@gildong.com", userDto.getEmail());
		assertEquals("홍길동", userDto.getName());
	}
}
