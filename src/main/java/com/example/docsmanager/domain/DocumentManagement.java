package com.example.docsmanager.domain;

import com.example.docsmanager.domain.entity.Document;
import java.time.Instant;
import java.util.List;
import java.util.Set;

/**
 * Document Management domain service.
 *
 * @author Vladimir.Conev
 */
public sealed interface DocumentManagement permits DocumentManager {
  List<Document> uploadDocuments(final List<Document> documents);

  void deleteDocuments(final Set<String> documentIds);

  byte[] getDocumentContent(final String id);

  Set<Document> getDocumentsByUserId(
      final String userId, final String extension, final Instant from, final Instant to);
}
