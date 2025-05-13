package com.mycom.myapp;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class DatePickerApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Test
	void contextLoads() {
		// 기본 컨텍스트 로드 테스트
		assertNotNull(applicationContext);
		assertNotNull(webApplicationContext);
	}

	@Test
	void 주요_빈_로드_테스트() {
		// 주요 빈들이 제대로 로드되는지 확인
		assertNotNull(applicationContext.getBean(PasswordEncoder.class));
	}

	@Test
	void 애플리케이션_시작_테스트() {
		// 애플리케이션이 오류 없이 시작되는지 테스트
		String[] args = new String[]{};
		DatePickerApplication.main(args);
	}

}
