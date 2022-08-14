package com.example.docsmanager.adapter.in;

import com.example.docsmanager.adapter.in.dto.DocumentMetadataResponseDto;
import com.example.docsmanager.domain.entity.Document;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.mime.MimeTypeException;
import org.springframework.web.multipart.MultipartFile;

public final class DocumentRestMapper {

  private DocumentRestMapper() {}

  public static DocumentMetadataResponseDto mapDocumentToDocumentMetadataResponseDto(
      final Document document) {
    return new DocumentMetadataResponseDto(
        document.id(),
        document.fileName(),
        document.extension(),
        document.userId(),
        document.creationDate().format(DateTimeFormatter.ISO_DATE_TIME));
  }

  public static Document mapMultipartFileToDocument(
      final MultipartFile multipartFile, final String userId) {
    var documentId = UUID.randomUUID().toString();
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
      throw new IllegalStateException("Media Type name is Invalid", ex);
    }
    byte[] content;
    try {
      content = IOUtils.toByteArray(multipartFile.getInputStream());
    } catch (IOException ioException) {
      throw new IllegalStateException("Error while extracting byte array content.", ioException);
    }
    return new Document(
        documentId,
        fileName,
        fileExtension,
        LocalDateTime.now(ZoneId.systemDefault()),
        content,
        userId);
  }

  public static List<Document> mapMultipartFilesToDocuments(
      final List<MultipartFile> multipartFiles, final String userId) {
    return multipartFiles.stream()
        .map(multipartFile -> mapMultipartFileToDocument(multipartFile, userId))
        .toList();
  }

  public static List<DocumentMetadataResponseDto> mapDocumentsToDocumentMetadataResponseDtos(
      final List<Document> documents) {
    return documents.stream()
        .map(DocumentRestMapper::mapDocumentToDocumentMetadataResponseDto)
        .toList();
  }
}
