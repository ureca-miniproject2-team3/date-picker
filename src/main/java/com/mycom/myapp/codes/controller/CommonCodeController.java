package com.mycom.myapp.codes.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycom.myapp.codes.dto.CommonCodeResultDto;
import com.mycom.myapp.codes.service.CommonCodeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "CommonCode", description = "공통코드 API")
public class CommonCodeController {

	private final CommonCodeService commonCodeService;
	
	@PostMapping("/commoncodes")
	@Operation(summary = "공통코드 조회", description = "공통코드 리스트에 맞는 코드 리스트를 불러옵니다.")
	public ResponseEntity<CommonCodeResultDto> getCommonCodeList(@RequestBody List<String> groupCodes) {
		CommonCodeResultDto commonCodeResultDto = commonCodeService.getCommonCodeList(groupCodes);
		
		if(Objects.equals(commonCodeResultDto.getResult(), "success")) {
			return ResponseEntity.ok(commonCodeResultDto);
		} 
		else {
			return ResponseEntity.internalServerError().body(commonCodeResultDto);
		}
	}
}
