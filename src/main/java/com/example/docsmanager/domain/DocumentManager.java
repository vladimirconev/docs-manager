package com.example.docsmanager.domain;

import java.util.Set;

import com.example.docsmanager.domain.entity.Document;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class DocumentManager implements DocumentManagement {

	private final DocumentManagementRepository docManagerRepository;
	
	@Override
	public Document uploadDocument(final Document document) {
		return docManagerRepository.uploadDocument(document);
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
