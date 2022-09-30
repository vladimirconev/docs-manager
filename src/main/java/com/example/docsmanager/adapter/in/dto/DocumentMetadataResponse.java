package com.example.docsmanager.adapter.in.dto;

public record DocumentMetadataResponse(
    String id, String fileName, String extension, String userId, String creationDate) {}
