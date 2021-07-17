package com.example.docsmanager.domain;

import java.util.List;
import java.util.Set;

import com.example.docsmanager.domain.entity.Document;

public interface DocumentManagementRepository {
	
	Document uploadDocument(final Document document);
	
	List<Document> uploadDocuments(final List<Document> documents);
	
	byte[] getDocumentContent(final String id);
	
	void deleteDocuments(final Set<String> documentIds);
	
	Set<Document> getAllDocumentsByUserId(final String userId, final String extension);
	
	

}
