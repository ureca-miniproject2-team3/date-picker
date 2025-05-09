package com.mycom.myapp.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

	private Long id;
	
	private String name;
	
	private String email;
	
	private String password;
	
}
