package com.example.docsmanager.adapter.out.db.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Document(indexName = "#{@documentIndexName}", createIndex = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentElasticDto implements Serializable {

  private static final long serialVersionUID = 307136746625657662L;

  @Id
  private String id;

  private String extension;

  private String fileName;

  private String creationDate;

  private byte[] content;

  private String userId;
}
