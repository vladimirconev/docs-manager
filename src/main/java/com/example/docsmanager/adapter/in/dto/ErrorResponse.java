package com.example.docsmanager.adapter.in.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
    String status,
    String code,
    String message,
    String path,
    String httpMethod,
    String exception,
    Instant timestamp)
    implements Serializable {}
