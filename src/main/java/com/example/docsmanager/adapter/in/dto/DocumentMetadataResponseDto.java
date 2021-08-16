package com.example.docsmanager.adapter.in.dto;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class DocumentMetadataResponseDto implements Serializable {

  private static final long serialVersionUID = -6766492733799269653L;

  private String id;

  private String fileName;

  private String extension;

  private String userId;

  private String creationDate;
}
