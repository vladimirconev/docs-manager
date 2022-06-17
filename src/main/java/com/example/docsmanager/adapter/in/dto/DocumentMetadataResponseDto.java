package com.example.docsmanager.adapter.in.dto;

public record DocumentMetadataResponseDto(
    String id, String fileName, String extension, String userId, String creationDate) {}
