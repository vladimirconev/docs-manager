package com.example.docsmanager.adapter.in;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.docsmanager.TestObjectFactory;
import com.example.docsmanager.adapter.in.dto.DocumentMetadataResponseDto;
import com.example.docsmanager.domain.entity.Document;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;

public class DocumentRestMapperTest extends TestObjectFactory {

  @Test
  void mapDocumentToDocumentMetadataResponseDtoTest() {
    Document document = buildDocumentInstance(
      DOCUMENT_ID,
      LocalDateTime.now(),
      BYTE_CONTENT,
      SAMPLE_USER_ID
    );

    DocumentMetadataResponseDto dto = DocumentRestMapper.mapDocumentToDocumentMetadataResponseDto(
      document
    );

    assertNotNull(dto);
    assertEquals(document.getId(), dto.id());
    assertEquals(document.getExtension(), dto.extension());
    assertEquals(document.getFileName(), dto.fileName());
    assertEquals(
      document.getCreationDate(),
      LocalDateTime.parse(dto.creationDate(), DateTimeFormatter.ISO_DATE_TIME)
    );
    assertEquals(document.getUserId(), dto.userId());
  }

  @Test
  void mapMultipartFileToDocumentTest() {
    Document document = DocumentRestMapper.mapMultipartFileToDocument(
      buildMockMultipartFile(),
      SAMPLE_USER_ID
    );

    assertNotNull(document);
    assertNotNull(document.getId());
    assertNotNull(document.getCreationDate());
    assertEquals(SAMPLE_USER_ID, document.getUserId());
  }

  @Test
  void mapMultipartFileToDocumentWhenPassingOnFaultyContentType() {
    Document document = DocumentRestMapper.mapMultipartFileToDocument(
      buildMockMultiplepartFileWithFaultyContentType(),
      SAMPLE_USER_ID
    );

    assertNotNull(document);
    assertNotNull(document.getId());
    assertNotNull(document.getCreationDate());
    assertEquals(SAMPLE_USER_ID, document.getUserId());
  }
}
