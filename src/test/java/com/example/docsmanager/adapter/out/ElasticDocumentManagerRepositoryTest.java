package com.example.docsmanager.adapter.out;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.docsmanager.TestObjectFactory;
import com.example.docsmanager.adapter.out.db.dto.DocumentElasticDto;

@ExtendWith(MockitoExtension.class)
public class ElasticDocumentManagerRepositoryTest extends TestObjectFactory {
	
	private ElasticDocumentManagerRepository elasticDocumentManagerRepo;
	
	@Mock
	private DocumentElasticRepository documentElasticRepository;
	
	@Mock
	private RestHighLevelClient restHighLevelClient;
	
	@BeforeEach
	void setup() {
		elasticDocumentManagerRepo =
				new ElasticDocumentManagerRepository(documentElasticRepository, restHighLevelClient,
						"test-drive");
	}
	
	@Test
	void uploadDocumentTest() {
		var documentElasticDto = buildDocumentElasticDto(DOCUMENT_ID, BYTE_CONTENT);
		Mockito.when(documentElasticRepository.save(Mockito.any(DocumentElasticDto.class)))
				.thenReturn(documentElasticDto);
		var document = buildDocumentInstance(DOCUMENT_ID, LocalDateTime.now(), BYTE_CONTENT);
		var uploadedDocument = elasticDocumentManagerRepo.uploadDocument(document);

		assertNotNull(uploadedDocument);
		assertEquals(documentElasticDto.getId(), uploadedDocument.getId());
		assertEquals(documentElasticDto.getUserId(), uploadedDocument.getUserId());
		assertEquals(documentElasticDto.getContent(), uploadedDocument.getContent());
		assertEquals(documentElasticDto.getFileName(), uploadedDocument.getFileName());
		assertEquals(documentElasticDto.getExtension(), uploadedDocument.getExtension());
		
		Mockito.verify(documentElasticRepository, times(1)).save(Mockito.any(DocumentElasticDto.class));
		Mockito.verifyNoMoreInteractions(documentElasticRepository);
	}
	
	@Test
	void getDocumentContentTest() {
		var documentElasticDto = buildDocumentElasticDto(DOCUMENT_ID, BYTE_CONTENT);
		
		Mockito.when(documentElasticRepository.findById(DOCUMENT_ID)).thenReturn(Optional.of(documentElasticDto));
		
		byte[] documentContent = elasticDocumentManagerRepo.getDocumentContent(DOCUMENT_ID);
		
		assertNotNull(documentContent);
		assertEquals(BYTE_CONTENT, documentContent);
		
		Mockito.verify(documentElasticRepository, times(1)).findById(DOCUMENT_ID);
		Mockito.verifyNoMoreInteractions(documentElasticRepository);
	}
	
	@Test
	void getDocumentContentShouldThrowNoSuchElementExceptionWhenItemIsNotFound() {
		Mockito.when(documentElasticRepository.findById(Mockito.anyString())).thenThrow(new NoSuchElementException());
		assertThrows(NoSuchElementException.class,
				() -> elasticDocumentManagerRepo.getDocumentContent(UUID.randomUUID().toString()));
	}
	
	@Test
	void deleteDocumentsTest() {
		
		elasticDocumentManagerRepo.deleteDocuments(Set.of(DOCUMENT_ID));
		
		Mockito.verify(documentElasticRepository, times(1)).deleteAllById(Set.of(DOCUMENT_ID));
	}
	
	
	
}