package com.example.docsmanager.domain.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Value;

@Value
public class Document implements Serializable {

  private static final long serialVersionUID = -7764718692153522958L;

  private final String id;

  private final String fileName;

  private final String extension;

  private final LocalDateTime creationDate;

  private final byte[] content;

  private final String userId;
}
