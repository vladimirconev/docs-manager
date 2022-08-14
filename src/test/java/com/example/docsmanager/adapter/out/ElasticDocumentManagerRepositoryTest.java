package com.example.docsmanager.adapter.out;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.example.docsmanager.TestObjectFactory;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ElasticDocumentManagerRepositoryTest extends TestObjectFactory {

  private ElasticDocumentManagerRepository elasticDocumentManagerRepo;

  @Mock private DocumentElasticRepository documentElasticRepository;

  @Mock private ElasticsearchClient esClient;

  @BeforeEach
  void setup() {
    elasticDocumentManagerRepo =
        new ElasticDocumentManagerRepository(documentElasticRepository, TEST_INDEX_NAME, esClient);
  }

  @AfterEach
  void tearDown() {
    elasticDocumentManagerRepo = null;
  }

  @Test
  void uploadDocumentsTest() {
    var documentElasticDto =
        buildDocumentElasticDto(
            DOCUMENT_ID,
            BYTE_CONTENT,
            LocalDateTime.now(Clock.systemUTC()),
            PNG_EXTENSION,
            FILE_NAME,
            SAMPLE_USER_ID);
    Mockito.when(documentElasticRepository.saveAll(Mockito.anyList()))
        .thenReturn(Set.of(documentElasticDto));
    var document =
        buildDocumentInstance(
            DOCUMENT_ID,
            LocalDateTime.now(Clock.systemUTC()),
            BYTE_CONTENT,
            SAMPLE_USER_ID,
            FILE_NAME,
            PNG_EXTENSION);
    var uploadedDocuments = elasticDocumentManagerRepo.uploadDocuments(List.of(document));

    assertNotNull(uploadedDocuments);
    assertThat(uploadedDocuments)
        .filteredOn(
            doc ->
                doc.id().equalsIgnoreCase(documentElasticDto.id())
                    && doc.userId().equalsIgnoreCase(documentElasticDto.userId())
                    && Base64.getEncoder()
                        .encodeToString(doc.content())
                        .equalsIgnoreCase(documentElasticDto.content())
                    && doc.fileName().equalsIgnoreCase(documentElasticDto.fileName())
                    && doc.extension().equalsIgnoreCase(documentElasticDto.extension()))
        .hasSize(1);

    Mockito.verify(documentElasticRepository, times(1)).saveAll(Mockito.anyList());
    Mockito.verifyNoMoreInteractions(documentElasticRepository);
  }

  @Test
  void getDocumentContentTest() {
    var documentElasticDto =
        buildDocumentElasticDto(
            DOCUMENT_ID,
            BYTE_CONTENT,
            LocalDateTime.now(Clock.systemUTC()),
            PNG_EXTENSION,
            FILE_NAME,
            SAMPLE_USER_ID);

    Mockito.when(documentElasticRepository.findById(DOCUMENT_ID))
        .thenReturn(Optional.of(documentElasticDto));

    byte[] documentContent = elasticDocumentManagerRepo.getDocumentContent(DOCUMENT_ID);

    assertNotNull(documentContent);
    assertArrayEquals(Base64.getDecoder().decode(documentElasticDto.content()), documentContent);

    Mockito.verify(documentElasticRepository, times(1)).findById(DOCUMENT_ID);
    Mockito.verifyNoMoreInteractions(documentElasticRepository);
  }

  @Test
  void getDocumentContentShouldThrowNoSuchElementExceptionWhenItemIsNotFound() {
    Mockito.when(documentElasticRepository.findById(Mockito.anyString()))
        .thenThrow(new NoSuchElementException());
    assertThrows(
        NoSuchElementException.class,
        () -> elasticDocumentManagerRepo.getDocumentContent(UUID.randomUUID().toString()));
  }

  @Test
  void deleteDocumentsTest() {
    elasticDocumentManagerRepo.deleteDocuments(Set.of(DOCUMENT_ID));

    Mockito.verify(documentElasticRepository, times(1)).deleteAllById(Set.of(DOCUMENT_ID));
  }
}
