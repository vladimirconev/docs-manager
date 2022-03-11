package com.example.docsmanager.adapter.out.db.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "#{@documentIndexName}", createIndex = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public record DocumentElasticDto(
  @Id String id, String extension, String fileName, String creationDate, String content, String userId
) {}
