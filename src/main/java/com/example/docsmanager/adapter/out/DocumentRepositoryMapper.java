package com.example.docsmanager.adapter.out;

import static java.time.ZoneOffset.UTC;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

import com.example.docsmanager.adapter.out.db.dto.DocumentElastic;
import com.example.docsmanager.domain.entity.Document;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public final class DocumentRepositoryMapper {

  private DocumentRepositoryMapper() {}

  public static DocumentElastic mapDocumentToDocumentElasticDto(final Document document) {
    return new DocumentElastic(
        document.id(),
        document.extension(),
        document.fileName(),
        document.creationDate().toString(),
        Base64.getEncoder().encodeToString(document.content()),
        document.userId());
  }

  public static Document mapDocumentElasticDtoToDocument(final DocumentElastic dto) {
    byte[] decodedContent = new byte[0];
    if (StringUtils.isNotBlank(dto.content())) {
      decodedContent = Base64.getDecoder().decode(dto.content());
    }
    return new Document(
        dto.id(),
        dto.fileName(),
        dto.extension(),
        LocalDateTime.parse(dto.creationDate(), ISO_DATE_TIME).toInstant(UTC),
        decodedContent,
        dto.userId());
  }

  public static List<DocumentElastic> mapDocumentsToDocumentElasticDtos(
      final List<Document> documents) {
    return documents.stream()
        .map(DocumentRepositoryMapper::mapDocumentToDocumentElasticDto)
        .toList();
  }
}
