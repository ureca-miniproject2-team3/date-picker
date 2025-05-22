package com.mycom.myapp.codes.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.util.Strings;
import org.aspectj.apache.bcel.classfile.Code;
import org.springframework.stereotype.Service;

import com.mycom.myapp.codes.dao.CommonCodeDao;
import com.mycom.myapp.codes.dto.CodeDto;
import com.mycom.myapp.codes.dto.CommonCodeResultDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommonCodeServiceImpl implements CommonCodeService {

	private final CommonCodeDao commonCodeDao;
	
	@Override
	public CommonCodeResultDto getCommonCodeList(List<String> groupCodes) {
		CommonCodeResultDto commonCodeResultDto = new CommonCodeResultDto();
		
		try {
			List<CodeDto> codeList = commonCodeDao.findByGroupCodes(groupCodes);
			Map<String, List<CodeDto>> commonCodeListMap = new HashMap<>();
			
			String curGroupCode = "";
			List<CodeDto> codeDtoList = null;
			
			for(CodeDto codeDto : codeList) {
				String groupCode = codeDto.getGroupCode();
				
				if(!Objects.equals(curGroupCode, groupCode)) {
					if(Strings.isNotEmpty(curGroupCode)) 
						commonCodeListMap.put(curGroupCode, codeDtoList);
					
					curGroupCode = groupCode;
					codeDtoList = new ArrayList<>();
				}
				
				codeDtoList.add(codeDto);
			}
			
			commonCodeListMap.put(curGroupCode, codeDtoList);
			
			commonCodeResultDto.setCommonCodeDtoListMap(commonCodeListMap);
			commonCodeResultDto.setResult("success");
		} catch (Exception e) {
			e.printStackTrace();
			commonCodeResultDto.setResult("fail");
		}
		
		return commonCodeResultDto;
	}

}
