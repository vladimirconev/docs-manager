package com.example.docsmanager.adapter.in;

import static org.junit.jupiter.api.Assertions.*;

import com.example.docsmanager.TestObjectFactory;
import com.example.docsmanager.adapter.in.dto.DocumentMetadataResponseDto;
import com.example.docsmanager.domain.entity.Document;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;

public class DocumentRestMapperTest extends TestObjectFactory {

  @Test
  void mapDocumentToDocumentMetadataResponseDtoTest() {
    Document document =
        buildDocumentInstance(
            DOCUMENT_ID,
            LocalDateTime.now(),
            BYTE_CONTENT,
            SAMPLE_USER_ID,
            FILE_NAME,
            PNG_EXTENSION);

    DocumentMetadataResponseDto dto =
        DocumentRestMapper.mapDocumentToDocumentMetadataResponseDto(document);

    assertNotNull(dto);
    assertEquals(document.id(), dto.id());
    assertEquals(document.extension(), dto.extension());
    assertEquals(document.fileName(), dto.fileName());
    assertEquals(
        document.creationDate(),
        LocalDateTime.parse(dto.creationDate(), DateTimeFormatter.ISO_DATE_TIME));
    assertEquals(document.userId(), dto.userId());
  }

  @Test
  void mapMultipartFileToDocumentTest() {
    Document document =
        DocumentRestMapper.mapMultipartFileToDocument(
            buildMockMultipartFile(IMAGE_PNG_CONTENT_TYPE, FILE_NAME, PNG_EXTENSION, BYTE_CONTENT),
            SAMPLE_USER_ID);

    assertNotNull(document);
    assertNotNull(document.id());
    assertNotNull(document.creationDate());
    assertEquals(SAMPLE_USER_ID, document.userId());
  }

  @Test
  void mapMultipartFileToDocumentWhenPassingOnFaultyContentType() {
    assertThrows(
        IllegalStateException.class,
        () ->
            DocumentRestMapper.mapMultipartFileToDocument(
                buildMockMultipartFile(PDF_CONTENT_TYPE, FILE_NAME, PNG_EXTENSION, BYTE_CONTENT),
                SAMPLE_USER_ID));
  }
}
