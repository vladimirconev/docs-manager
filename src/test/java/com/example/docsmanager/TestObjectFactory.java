package com.example.docsmanager;

import com.example.docsmanager.adapter.out.db.dto.DocumentElasticDto;
import com.example.docsmanager.domain.entity.Document;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class TestObjectFactory {

  protected byte[] BYTE_CONTENT = new byte[] { 1, 2, 3, 8 };
  protected String PNG_EXTENSION = "png";
  protected String FILE_NAME = "test_file";
  protected String SAMPLE_USER_ID = "foo";
  protected String DOCUMENT_ID = UUID.randomUUID().toString();

  protected Document buildDocumentInstance(
    final String id,
    final LocalDateTime creationDate,
    final byte[] content,
    final String user
  ) {
    return new Document(id, FILE_NAME, PNG_EXTENSION, creationDate, content, user);
  }

  protected DocumentElasticDto buildDocumentElasticDto(
    final String id,
    final byte[] content,
    final LocalDateTime when
  ) {
    var documentElasticDto = new DocumentElasticDto();
    documentElasticDto.setContent(content);
    documentElasticDto.setExtension(PNG_EXTENSION);
    documentElasticDto.setFileName(FILE_NAME);
    documentElasticDto.setUserId(SAMPLE_USER_ID);
    documentElasticDto.setId(id);
    documentElasticDto.setCreationDate(when.format(DateTimeFormatter.ISO_DATE_TIME));
    return documentElasticDto;
  }

  protected MultipartFile buildMockMultipartFile() {
    return new MockMultipartFile(
      FILE_NAME,
      FILE_NAME.concat(".").concat(PNG_EXTENSION),
      "image/png",
      BYTE_CONTENT
    );
  }

  protected MultipartFile buildMockMultiplepartFileWithFaultyContentType() {
    return new MockMultipartFile(
      FILE_NAME,
      FILE_NAME.concat(".").concat(PNG_EXTENSION),
      "pdf",
      BYTE_CONTENT
    );
  }
}
