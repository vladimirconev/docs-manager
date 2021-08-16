package com.example.docsmanager.adapter.in.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseDto implements Serializable {

  private static final long serialVersionUID = -889196517618118878L;

  private String status;

  private String code;

  private String message;

  private String path;

  private String httpMethod;

  private String exception;

  @JsonFormat(pattern = "yyyy-MM-ddTHH:mm:SS.ssZ")
  private String timestamp;
}
