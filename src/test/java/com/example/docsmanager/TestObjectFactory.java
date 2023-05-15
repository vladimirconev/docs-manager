package com.example.docsmanager;

import com.example.docsmanager.adapter.out.db.dto.DocumentElastic;
import com.example.docsmanager.domain.entity.Document;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

/** Facilitate creation of dummy test objects. */
@SuppressWarnings("SameParameterValue")
public class TestObjectFactory {

  /** Constants */
  protected final byte[] BYTE_CONTENT = "TEST".getBytes(StandardCharsets.UTF_8);

  protected final String PNG_EXTENSION = "png";
  protected final String FILE_NAME = "test_file";
  protected final String SAMPLE_USER_ID = "foo";
  protected final String DOCUMENT_ID = UUID.randomUUID().toString();
  protected final String SAMPLE_DOCUMENT_ID = UUID.randomUUID().toString();
  protected final String PDF_CONTENT_TYPE = "pdf";
  protected final String IMAGE_PNG_CONTENT_TYPE = "image/png";
  protected final String TEST_INDEX_NAME = "test-drive";

  protected Document buildDocumentInstance(
      final String id,
      final Instant creationDate,
      final byte[] content,
      final String user,
      final String fileName,
      final String extension) {
    return new Document(id, fileName, extension, creationDate, content, user);
  }

  protected DocumentElastic buildDocumentElasticDto(
      final String id,
      final byte[] content,
      final LocalDateTime when,
      final String extension,
      final String fileName,
      final String userId) {
    return new DocumentElastic(
        id,
        extension,
        fileName,
        when.format(DateTimeFormatter.ISO_DATE_TIME),
        Base64.getEncoder().encodeToString(content),
        userId);
  }

  protected MultipartFile buildMockMultipartFile(
      final String contentType,
      final String fileName,
      final String extension,
      final byte[] content) {
    return new MockMultipartFile(
        fileName, String.format("%s.%s", fileName, extension), contentType, content);
  }
}
