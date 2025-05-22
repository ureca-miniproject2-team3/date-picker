package com.mycom.myapp.codes.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CodeDto {

    private String groupCode;
    private String code;
    private String codeName;
    private String codeNameBrief;
    private Integer orderNo;

}
