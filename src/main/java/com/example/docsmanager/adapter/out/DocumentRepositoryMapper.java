package com.example.docsmanager.adapter.out;

import com.example.docsmanager.adapter.out.db.dto.DocumentElasticDto;
import com.example.docsmanager.domain.entity.Document;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DocumentRepositoryMapper {

  private DocumentRepositoryMapper() {}

  public static DocumentElasticDto mapDocumentToDocumentElasticDto(
    final Document document
  ) {
    var dto = new DocumentElasticDto();
    dto.setContent(document.content());
    dto.setCreationDate(document.creationDate().format(DateTimeFormatter.ISO_DATE_TIME));
    dto.setExtension(document.extension());
    dto.setFileName(document.fileName());
    dto.setId(document.id());
    dto.setUserId(document.userId());
    return dto;
  }

  public static Document mapDocumentElasticDtoToDocument(final DocumentElasticDto dto) {
    return new Document(
      dto.getId(),
      dto.getFileName(),
      dto.getExtension(),
      LocalDateTime.parse(dto.getCreationDate(), DateTimeFormatter.ISO_DATE_TIME),
      dto.getContent(),
      dto.getUserId()
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
