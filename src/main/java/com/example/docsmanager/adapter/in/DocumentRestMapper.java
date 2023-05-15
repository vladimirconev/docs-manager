package com.example.docsmanager.adapter.in;

import static java.time.ZoneOffset.UTC;
import static java.util.UUID.randomUUID;

import com.example.docsmanager.adapter.in.dto.DocumentMetadataResponse;
import com.example.docsmanager.domain.entity.Document;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.mime.MimeTypeException;
import org.springframework.web.multipart.MultipartFile;

public final class DocumentRestMapper {

  private DocumentRestMapper() {}

  public static DocumentMetadataResponse mapDocumentToDocumentMetadataResponseDto(
      final Document document) {
    return new DocumentMetadataResponse(
        document.id(),
        document.fileName(),
        document.extension(),
        document.userId(),
        document.creationDate().toString());
  }

  public static Document mapMultipartFileToDocument(
      final MultipartFile multipartFile, final String userId) {
    var documentId = randomUUID().toString();
    var fileName = StringUtils.substringBeforeLast(multipartFile.getOriginalFilename(), ".");
    var fileExtension = multipartFile.getContentType();
    try {
      var apacheTikaConfig = TikaConfig.getDefaultConfig();
      var extension =
          apacheTikaConfig
              .getMimeRepository()
              .forName(multipartFile.getContentType())
              .getExtension();
      fileExtension = StringUtils.substringAfterLast(extension, ".");
    } catch (MimeTypeException ex) {
      throw new IllegalArgumentException("Media Type name is Invalid", ex);
    }
    byte[] content;
    try {
      content = IOUtils.toByteArray(multipartFile.getInputStream());
    } catch (IOException ioException) {
      throw new IllegalStateException("Error while extracting byte array content.", ioException);
    }
    Clock clock = Clock.fixed(Instant.EPOCH, UTC);
    return new Document(documentId, fileName, fileExtension, clock.instant(), content, userId);
  }

  public static List<Document> mapMultipartFilesToDocuments(
      final List<MultipartFile> multipartFiles, final String userId) {
    return multipartFiles.stream()
        .map(multipartFile -> mapMultipartFileToDocument(multipartFile, userId))
        .toList();
  }

  public static List<DocumentMetadataResponse> mapDocumentsToDocumentMetadataResponseDtos(
      final List<Document> documents) {
    return documents.stream()
        .map(DocumentRestMapper::mapDocumentToDocumentMetadataResponseDto)
        .toList();
  }
}
