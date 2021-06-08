package com.example.docsmanager.adapter.out.db.dto;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Document(indexName = "#{@documentIndexName}", createIndex = false)
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
