package com.mycom.myapp.codes.dto;

import java.util.List;
import java.util.Map;

import com.mycom.myapp.codes.dto.CodeDto;

import lombok.Data;

@Data
public class CommonCodeResultDto {

	private String result;
	
	private Map<String, List<CodeDto>> commonCodeDtoListMap;
	
}
