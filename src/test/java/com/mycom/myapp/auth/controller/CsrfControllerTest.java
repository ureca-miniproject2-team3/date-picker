package com.mycom.myapp.auth.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class CsrfControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Test
	public void csrfTest() throws Exception {
		this.mockMvc.perform(get("/api/auth/csrf-token"))
			.andExpect(status().isOk());
	}
}
