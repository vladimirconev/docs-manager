package com.example.docsmanager.domain;

import com.example.docsmanager.domain.entity.Document;
import java.time.Instant;
import java.util.List;
import java.util.Set;

public interface DocumentManagementRepository {
  List<Document> uploadDocuments(final List<Document> documents);

  byte[] getDocumentContent(final String id);

  void deleteDocuments(final Set<String> documentIds);

  Set<Document> getAllDocumentsByUserId(
      final String userId, final String extension, final Instant from, final Instant to);
}
