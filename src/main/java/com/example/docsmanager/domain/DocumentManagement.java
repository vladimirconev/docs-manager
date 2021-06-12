package com.example.docsmanager.domain;

import java.util.Set;

import com.example.docsmanager.domain.entity.Document;

/**
 * Document Management domain service.
 * 
 * @author Vladimir.Conev
 *
 */
public interface DocumentManagement {
	
	Document uploadDocument(final Document document);
	
	void deleteDocuments(final Set<String> documentIds);
	
	byte[] getDocumentContent(final String id);
	
	Set<Document> getDocumentsByUserId(final String userId, final String extension);

}
