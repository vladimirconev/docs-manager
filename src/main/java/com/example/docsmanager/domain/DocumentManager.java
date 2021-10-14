package com.example.docsmanager.domain;

import com.example.docsmanager.domain.entity.Document;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public non-sealed class DocumentManager implements DocumentManagement {

  private final DocumentManagementRepository docManagerRepository;

  @Override
  public List<Document> uploadDocuments(final List<Document> documents) {
    return docManagerRepository.uploadDocuments(documents);
  }

  @Override
  public void deleteDocuments(final Set<String> documentIds) {
    docManagerRepository.deleteDocuments(documentIds);
  }

  @Override
  public byte[] getDocumentContent(final String id) {
    return docManagerRepository.getDocumentContent(id);
  }

  @Override
  public Set<Document> getDocumentsByUserId(final String userId, final String extension) {
    return docManagerRepository.getAllDocumentsByUserId(userId, extension);
  }
}
