package com.example.docsmanager.adapter.in.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponseDto(
    String status,
    String code,
    String message,
    String path,
    String httpMethod,
    String exception,
    @JsonFormat(pattern = "yyyy-MM-ddTHH:mm:SS.ssZ") String timestamp) {}
