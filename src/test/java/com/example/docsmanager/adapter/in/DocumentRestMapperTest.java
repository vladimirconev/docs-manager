package com.example.docsmanager.adapter.in;

import static org.junit.jupiter.api.Assertions.*;

import com.example.docsmanager.TestObjectFactory;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;

public class DocumentRestMapperTest extends TestObjectFactory {

  @Test
  void mapDocumentToDocumentMetadataResponseDtoTest() {
    var document =
        buildDocumentInstance(
            DOCUMENT_ID,
            LocalDateTime.now(Clock.systemUTC()),
            BYTE_CONTENT,
            SAMPLE_USER_ID,
            FILE_NAME,
            PNG_EXTENSION);

    var documentMetadata = DocumentRestMapper.mapDocumentToDocumentMetadataResponseDto(document);

    assertNotNull(documentMetadata);
    assertEquals(document.id(), documentMetadata.id());
    assertEquals(document.extension(), documentMetadata.extension());
    assertEquals(document.fileName(), documentMetadata.fileName());
    assertEquals(
        document.creationDate(),
        LocalDateTime.parse(documentMetadata.creationDate(), DateTimeFormatter.ISO_DATE_TIME));
    assertEquals(document.userId(), documentMetadata.userId());
  }

  @Test
  void mapMultipartFileToDocumentTest() {
    var document =
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
        IllegalArgumentException.class,
        () ->
            DocumentRestMapper.mapMultipartFileToDocument(
                buildMockMultipartFile(PDF_CONTENT_TYPE, FILE_NAME, PNG_EXTENSION, BYTE_CONTENT),
                SAMPLE_USER_ID));
  }
}
