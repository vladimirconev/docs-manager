package com.example.docsmanager.adapter.in;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

import com.example.docsmanager.TestObjectFactory;
import com.example.docsmanager.adapter.in.dto.DocumentMetadataResponseDto;
import com.example.docsmanager.domain.entity.Document;

public class DocumentRestMapperTest extends TestObjectFactory {

	@Test
	void mapDocumentToDocumentMetadataResponseDtoTest() {
		Document document = buildDocumentInstance(DOCUMENT_ID, LocalDateTime.now(), BYTE_CONTENT);

		DocumentMetadataResponseDto dto = DocumentRestMapper.mapDocumentToDocumentMetadataResponseDto(document);

		assertNotNull(dto);
		assertEquals(document.getId(), dto.getId());
		assertEquals(document.getExtension(), dto.getExtension());
		assertEquals(document.getFileName(), dto.getFileName());
		assertEquals(document.getCreationDate(), LocalDateTime.parse(dto.getCreationDate(), DateTimeFormatter.ISO_DATE_TIME));
		assertEquals(document.getUserId(), dto.getUserId());
	}
	
	@Test
	void mapMultipartFileToDocumentTest() {
		Document document = DocumentRestMapper.mapMultipartFileToDocument(buildMockMultipartFile(), SAMPLE_USER_ID);
		
		assertNotNull(document);
		assertNotNull(document.getId());
		assertNotNull(document.getCreationDate());
		assertEquals(SAMPLE_USER_ID, document.getUserId());
		
	}
	
	@Test
	void mapMultipartFileToDocumentWhenPassingOnFaultyContentType() {
		Document document = DocumentRestMapper.mapMultipartFileToDocument(buildMockMultiplepartFileWithFaultyContentType(), SAMPLE_USER_ID);

		assertNotNull(document);
		assertNotNull(document.getId());
		assertNotNull(document.getCreationDate());
		assertEquals(SAMPLE_USER_ID, document.getUserId());
	}
	
}
