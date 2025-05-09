package com.mycom.myapp.users.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {

	private int id;
	
	private String name;
	
	private String email;
	
	private String password;
	
}
