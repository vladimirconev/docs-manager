package com.example.docsmanager.adapter.in;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.elasticsearch.common.collect.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.example.docsmanager.TestObjectFactory;
import com.example.docsmanager.adapter.in.dto.DocumentMetadataResponseDto;
import com.example.docsmanager.domain.DocumentManager;
import com.example.docsmanager.domain.entity.Document;

@ExtendWith(MockitoExtension.class)
public class DocumentRestControllerTest extends TestObjectFactory {
	
	@InjectMocks
	private DocumentRestController documentRestController;
	
	@Mock
	private DocumentManager documentManager;
	
	@Test
	void getDocumentContentTest() {
		byte[] content = new byte[] {1,2,3,8};
		String documentId = UUID.randomUUID().toString();
		Mockito.when(documentManager.getDocumentContent(documentId)).thenReturn(content);
		
		ResponseEntity<byte[]> responseEntity = documentRestController.getDocumentContent(documentId);
		
		assertNotNull(responseEntity);
		assertNotNull(responseEntity.getBody());
		assertEquals(content,responseEntity.getBody());
		
		Mockito.verify(documentManager, times(1)).getDocumentContent(documentId);
		Mockito.verifyNoMoreInteractions(documentManager);
	}
	
	@Test
	void uploadDocumentTest() {
		MultipartFile multipartFile = buildMockMultipartFile();
		MultipartFile[] multipartFiles = {multipartFile};
		Document sampleDocument = buildDocumentInstance(DOCUMENT_ID, LocalDateTime.now(), BYTE_CONTENT);
		Mockito.when(documentManager.uploadDocuments(Mockito.anyList())).thenReturn(Arrays.asList(sampleDocument));

		ResponseEntity<List<DocumentMetadataResponseDto>> responseEntity = documentRestController
				.uploadDocuments(multipartFiles, SAMPLE_USER_ID);

		assertNotNull(responseEntity);
		assertNotNull(responseEntity.getBody());
		assertTrue(responseEntity.getBody().size() == 1);
		
		DocumentMetadataResponseDto documentMetadataResponseDto = responseEntity.getBody().stream().findFirst().get();
		
		assertEquals(PNG_EXTENSION, documentMetadataResponseDto.getExtension());
		assertEquals(FILE_NAME, documentMetadataResponseDto.getFileName());
		assertEquals(SAMPLE_USER_ID, documentMetadataResponseDto.getUserId());
		assertNotNull(documentMetadataResponseDto.getCreationDate());
		assertNotNull(sampleDocument.getId(), documentMetadataResponseDto.getId());

		Mockito.verify(documentManager, times(1)).uploadDocuments(Mockito.anyList());
		Mockito.verifyNoMoreInteractions(documentManager);
	}
	
	@Test
	void uploadDocumentShouldThrowIllegalStateExceptionWhenConversionToByteArrayFails() throws IOException {
		MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
		Mockito.when(multipartFile.getContentType()).thenReturn("image/png");
		Mockito.when(multipartFile.getOriginalFilename()).thenReturn(FILE_NAME.concat(".").concat(PNG_EXTENSION));
		Mockito.when(multipartFile.getInputStream()).thenThrow(IOException.class);
		MultipartFile[] multipartFiles = {multipartFile};
		assertThrows(IllegalStateException.class, () -> documentRestController.uploadDocuments(
				multipartFiles,
				SAMPLE_USER_ID));
	}
	
	@Test
	void deleteDocumentByIdsTest() {
		ResponseEntity<Void> responseEntity = documentRestController.deleteDocuments(Set.of(DOCUMENT_ID));
		
		assertNotNull(responseEntity);
		
		Mockito.verify(documentManager, times(1)).deleteDocuments(Set.of(DOCUMENT_ID));
		Mockito.verifyNoMoreInteractions(documentManager);
	}
	
}
