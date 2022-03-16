package com.example.docsmanager;

import com.example.docsmanager.adapter.out.db.dto.DocumentElasticDto;
import com.example.docsmanager.domain.entity.Document;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class TestObjectFactory {

  protected final byte[] BYTE_CONTENT = "TEST".getBytes(StandardCharsets.UTF_8);
  protected final String PNG_EXTENSION = "png";
  protected final String FILE_NAME = "test_file";
  protected final String SAMPLE_USER_ID = "foo";
  protected final String DOCUMENT_ID = UUID.randomUUID().toString();
  protected final String SAMPLE_DOCUMENT_ID = UUID.randomUUID().toString();
  protected final String PDF_CONTENT_TYPE = "pdf";

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
    return new DocumentElasticDto(
      id,
      PNG_EXTENSION,
      FILE_NAME,
      when.format(DateTimeFormatter.ISO_DATE_TIME),
      new String(content, StandardCharsets.UTF_8),
      SAMPLE_USER_ID
    );
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
      PDF_CONTENT_TYPE,
      BYTE_CONTENT
    );
  }
}
