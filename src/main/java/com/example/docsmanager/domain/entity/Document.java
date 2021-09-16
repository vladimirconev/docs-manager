package com.example.docsmanager.domain.entity;

import java.time.LocalDateTime;

public record Document(
  String id, String fileName, String extension, LocalDateTime creationDate, byte[] content, String userId
) {}
