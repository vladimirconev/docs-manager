package com.example.docsmanager.adapter.out;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.docsmanager.DocsElasticsearchContainer;
import com.example.docsmanager.TestObjectFactory;
import com.example.docsmanager.boot.DocsManagerApplication;
import com.example.docsmanager.domain.entity.Document;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Integration tests for {@link ElasticDocumentManagerRepository} using
 * TestContainers.
 *
 * @author Vladimir.Conev
 *
 */
@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { DocsManagerApplication.class })
public class ElasticDocumentManagerRepositoryIT extends TestObjectFactory {

  @Container
  private static ElasticsearchContainer elasticsearchContainer = new DocsElasticsearchContainer();

  private static final String IT_DEMO_USER = "integration_test_demo_user";

  @Autowired
  private ElasticDocumentManagerRepository esDocsManagerRepo;

  @BeforeAll
  static void setUp() {
    elasticsearchContainer.start();
  }

  @BeforeEach
  void testIsContainerRunning() {
    assertTrue(elasticsearchContainer.isRunning());
  }

  @AfterAll
  static void destroy() {
    elasticsearchContainer.stop();
  }

  @Test
  void uploadDocumentTest() {
    String id = UUID.randomUUID().toString();
    var sampleDocument = buildDocumentInstance(
      id,
      LocalDateTime.now(),
      BYTE_CONTENT,
      IT_DEMO_USER
    );
    var uploadedDocument = esDocsManagerRepo.uploadDocument(sampleDocument);

    assertNotNull(uploadedDocument);
    assertEquals(sampleDocument.id(), uploadedDocument.id());
    assertEquals(sampleDocument.extension(), uploadedDocument.extension());
    assertEquals(sampleDocument.fileName(), uploadedDocument.fileName());
    assertEquals(sampleDocument.userId(), uploadedDocument.userId());

    byte[] documentContent = esDocsManagerRepo.getDocumentContent(uploadedDocument.id());

    assertNotNull(documentContent);
    assertArrayEquals(BYTE_CONTENT, documentContent);
  }

  @Test
  void getDocumentContentTest() {
    var sampleDocument = buildDocumentInstance(
      UUID.randomUUID().toString(),
      LocalDateTime.now(),
      BYTE_CONTENT,
      IT_DEMO_USER
    );
    var uploadedDocument = esDocsManagerRepo.uploadDocument(sampleDocument);

    assertNotNull(uploadedDocument);

    byte[] documentContent = esDocsManagerRepo.getDocumentContent(uploadedDocument.id());

    assertNotNull(documentContent);
    assertArrayEquals(BYTE_CONTENT, documentContent);
  }

  @Test
  void deleteDocumentsTest() {
    List<Document> uploadDocuments = uploadDocuments(
      buildDocumentInstance(
        UUID.randomUUID().toString(),
        LocalDateTime.now(),
        BYTE_CONTENT,
        IT_DEMO_USER
      ),
      buildDocumentInstance(
        UUID.randomUUID().toString(),
        LocalDateTime.now(),
        BYTE_CONTENT,
        IT_DEMO_USER
      )
    );

    Set<String> documentIds = uploadDocuments
      .stream()
      .map(Document::id)
      .collect(Collectors.toSet());

    esDocsManagerRepo.deleteDocuments(documentIds);

    documentIds.forEach(
      documentId -> {
        assertThrows(
          NoSuchElementException.class,
          () -> esDocsManagerRepo.getDocumentContent(documentId)
        );
      }
    );
  }

  @Test
  void uploadMultipleDocumentsTest() {
    List<Document> uploadDocuments = uploadDocuments(
      buildDocumentInstance(
        UUID.randomUUID().toString(),
        LocalDateTime.now(),
        BYTE_CONTENT,
        IT_DEMO_USER
      ),
      buildDocumentInstance(
        UUID.randomUUID().toString(),
        LocalDateTime.now(),
        BYTE_CONTENT,
        IT_DEMO_USER
      )
    );

    Set<String> documentIds = uploadDocuments
      .stream()
      .map(Document::id)
      .collect(Collectors.toSet());

    documentIds.forEach(
      documentId -> {
        assertNotNull(esDocsManagerRepo.getDocumentContent(documentId));
      }
    );
  }

  @Test
  void getAllDocumentsByUserIdTest() {
    uploadDocuments(
      buildDocumentInstance(
        UUID.randomUUID().toString(),
        LocalDateTime.now(),
        BYTE_CONTENT,
        IT_DEMO_USER
      ),
      buildDocumentInstance(
        UUID.randomUUID().toString(),
        LocalDateTime.now(),
        BYTE_CONTENT,
        IT_DEMO_USER
      )
    );
    Set<Document> allDocumentsByUserIdWithPNGExtension = esDocsManagerRepo.getAllDocumentsByUserId(
      IT_DEMO_USER,
      PNG_EXTENSION
    );

    assertNotNull(allDocumentsByUserIdWithPNGExtension);
    assertFalse(allDocumentsByUserIdWithPNGExtension.isEmpty());

    Set<Document> allDocumentsByUserIdWithPDFExtension = esDocsManagerRepo.getAllDocumentsByUserId(
      IT_DEMO_USER,
      "pdf"
    );

    assertNotNull(allDocumentsByUserIdWithPDFExtension);
    assertTrue(allDocumentsByUserIdWithPDFExtension.isEmpty());

    Set<Document> allDocumentsByUserId = esDocsManagerRepo.getAllDocumentsByUserId(
      IT_DEMO_USER,
      null
    );

    assertNotNull(allDocumentsByUserId);
    assertTrue(
      allDocumentsByUserIdWithPNGExtension.size() == allDocumentsByUserId.size()
    );
  }

  private List<Document> uploadDocuments(Document... docs) {
    return esDocsManagerRepo.uploadDocuments(Arrays.asList(docs));
  }
}
