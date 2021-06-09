package com.example.docsmanager.adapter.out;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.example.docsmanager.adapter.out.db.dto.DocumentElasticDto;
import com.example.docsmanager.domain.entity.Document;

public class DocumentRepositoryMapper {

	private DocumentRepositoryMapper() {
		
	}
	
	public static DocumentElasticDto mapDocumentToDocumentElasticDto(final Document document) {
		var dto = new DocumentElasticDto();
		dto.setContent(document.getContent());
		dto.setCreationDate(document.getCreationDate().format(DateTimeFormatter.ISO_DATE_TIME));
		dto.setExtension(document.getExtension());
		dto.setFileName(document.getFileName());
		dto.setId(document.getId());
		dto.setUserId(document.getUserId());
		return dto;
	}
	
	public static Document mapDocumentElasticDtoToDocument(final DocumentElasticDto dto) {
		return new Document(dto.getId(), dto.getFileName(), dto.getExtension(),
				LocalDateTime.parse(dto.getCreationDate(), DateTimeFormatter.ISO_DATE_TIME),dto.getContent(),
				dto.getUserId());
	}

}
