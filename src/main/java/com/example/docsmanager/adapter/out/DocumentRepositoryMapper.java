package com.example.docsmanager.adapter.out;

import com.example.docsmanager.adapter.out.db.dto.DocumentElasticDto;
import com.example.docsmanager.domain.entity.Document;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class DocumentRepositoryMapper {

  private DocumentRepositoryMapper() {}

  public static DocumentElasticDto mapDocumentToDocumentElasticDto(
    final Document document
  ) {
    return new DocumentElasticDto(
      document.id(),
      document.extension(),
      document.fileName(),
      document.creationDate().format(DateTimeFormatter.ISO_DATE_TIME),
      Base64.getEncoder().encodeToString(document.content()),
      document.userId()
    );
  }

  public static Document mapDocumentElasticDtoToDocument(final DocumentElasticDto dto) {
    byte[] decodedContent = new byte[0];
    if (StringUtils.isNotBlank(dto.content())) {
      decodedContent = Base64.getDecoder().decode(dto.content());
    }
    return new Document(
      dto.id(),
      dto.fileName(),
      dto.extension(),
      LocalDateTime.parse(dto.creationDate(), DateTimeFormatter.ISO_DATE_TIME),
      decodedContent,
      dto.userId()
    );
  }

  public static List<DocumentElasticDto> mapDocumentsToDocumentElasticDtos(
    final List<Document> documents
  ) {
    return documents
      .parallelStream()
      .map(DocumentRepositoryMapper::mapDocumentToDocumentElasticDto)
      .toList();
  }
}
