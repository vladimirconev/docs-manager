package com.example.docsmanager.domain.entity;

import java.time.Instant;

public record Document(
    String id,
    String fileName,
    String extension,
    Instant creationDate,
    byte[] content,
    String userId) {}
