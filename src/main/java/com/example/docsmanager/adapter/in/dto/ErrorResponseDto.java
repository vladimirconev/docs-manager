package com.example.docsmanager.adapter.in.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseDto implements Serializable {
	
	private static final long serialVersionUID = -889196517618118878L;

	private String code;
	
	private String status;
	
	private String httpMethod;
	
	private String exception;
	
	private String path;
	
	private String message;
	
	@JsonFormat(pattern = "yyyy-MM-ddTHH:mm:SS.ssZ")
	private String timestamp;

}
