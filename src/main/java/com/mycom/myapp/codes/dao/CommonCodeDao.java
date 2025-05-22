package com.mycom.myapp.codes.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.mycom.myapp.codes.dto.CodeDto;

@Mapper
public interface CommonCodeDao {

	List<CodeDto> findByGroupCodes(@Param("groupCodes") List<String> groupCodes);
	
}
