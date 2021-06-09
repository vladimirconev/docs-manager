package com.example.docsmanager.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;

import java.time.LocalDateTime;

import org.elasticsearch.common.collect.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.docsmanager.TestObjectFactory;
import com.example.docsmanager.domain.entity.Document;

@ExtendWith(MockitoExtension.class)
public class DocumentManagerTest extends TestObjectFactory {
	
	@InjectMocks
	private DocumentManager documentManager;
	
	@Mock
	private DocumentManagementRepository docsManagementRepo;
	
	
	@Test
	void uploadDocumentTest() {
		Document document = buildDocumentInstance(DOCUMENT_ID, LocalDateTime.now(), BYTE_CONTENT);
		Mockito.when(docsManagementRepo.uploadDocument(Mockito.any(Document.class))).thenReturn(document);
		
		Document output = documentManager.uploadDocument(document);
		
		assertNotNull(output);
		assertEquals(document, output);
		
		Mockito.verify(docsManagementRepo, times(1)).uploadDocument(Mockito.any(Document.class));
		Mockito.verifyNoMoreInteractions(docsManagementRepo);
	}
	
	@Test
	void deleteDocumentsTest() {
		documentManager.deleteDocuments(Set.of(DOCUMENT_ID));
		
		Mockito.verify(docsManagementRepo, times(1)).deleteDocuments(Mockito.anySet());
		Mockito.verifyNoMoreInteractions(docsManagementRepo);
		
	}
	
	@Test
	void getDocumentContentTest() {
		Mockito.when(docsManagementRepo.getDocumentContent(DOCUMENT_ID)).thenReturn(BYTE_CONTENT);
		
		byte[] documentContent = documentManager.getDocumentContent(DOCUMENT_ID);
		
		assertNotNull(documentContent);
		assertEquals(BYTE_CONTENT, documentContent);
		
		Mockito.verify(docsManagementRepo, times(1)).getDocumentContent(DOCUMENT_ID);
		Mockito.verifyNoMoreInteractions(docsManagementRepo);
	}

}
