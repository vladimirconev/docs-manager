package com.example.docsmanager.domain;

import java.util.Set;

import com.example.docsmanager.domain.entity.Document;

public interface DocumentManagementRepository {
	
	Document uploadDocument(final Document document);
	
	byte[] getDocumentContent(final String id);
	
	void deleteDocuments(final Set<String> documentIds);
	
	Set<Document> getAllDocumentsByUserId(final String userId);
	
	

}
