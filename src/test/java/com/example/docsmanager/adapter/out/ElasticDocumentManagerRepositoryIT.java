package com.example.docsmanager.adapter.out;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import com.example.docsmanager.DocsElasticsearchContainer;
import com.example.docsmanager.TestObjectFactory;
import com.example.docsmanager.boot.DocsManagerApplication;
import com.example.docsmanager.domain.entity.Document;
import java.io.IOException;
import java.time.Clock;
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
 * Integration tests for {@link ElasticDocumentManagerRepository} using TestContainers.
 *
 * @author Vladimir.Conev
 */
@Testcontainers(disabledWithoutDocker = true)
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {DocsManagerApplication.class})
public class ElasticDocumentManagerRepositoryIT extends TestObjectFactory {

  @Container
  private static final ElasticsearchContainer elasticsearchContainer =
      new DocsElasticsearchContainer();

  private static final String IT_DEMO_USER = "integration_test_demo_user";
  private static final String EXPLICIT_MAPPINGS_JSON_PATH = "/explicit_mappings.json";

  @Autowired private ElasticDocumentManagerRepository esDocsManagerRepo;

  @Autowired private ElasticsearchClient esClient;

  @Autowired private String documentIndexName;

  @BeforeAll
  static void setUp() {
    elasticsearchContainer.start();
  }

  @BeforeEach
  void testIsContainerRunning() throws IOException {
    assertTrue(elasticsearchContainer.isRunning());
    // Create Index + Mappings
    BooleanResponse booleanResponse = esClient.indices().exists(e -> e.index(documentIndexName));
    if (!booleanResponse.value()) {
      var inputStream = this.getClass().getResourceAsStream(EXPLICIT_MAPPINGS_JSON_PATH);
      var createIndexResponse =
          esClient
              .indices()
              .create(c -> c.index(documentIndexName).mappings(m -> m.withJson(inputStream)));
      assertNotNull(createIndexResponse);
      assertEquals(Boolean.TRUE, createIndexResponse.acknowledged());
    }
    // Load sample data
    List<Document> uploadDocuments =
        uploadDocuments(
            buildDocumentInstance(
                DOCUMENT_ID,
                LocalDateTime.now(Clock.systemUTC()),
                BYTE_CONTENT,
                IT_DEMO_USER,
                FILE_NAME,
                PNG_EXTENSION),
            buildDocumentInstance(
                SAMPLE_DOCUMENT_ID,
                LocalDateTime.now(Clock.systemUTC()),
                BYTE_CONTENT,
                IT_DEMO_USER,
                FILE_NAME,
                PNG_EXTENSION));

    Set<String> documentIds =
        uploadDocuments.stream().map(Document::id).collect(Collectors.toSet());
    assertEquals(documentIds, Set.of(DOCUMENT_ID, SAMPLE_DOCUMENT_ID));
  }

  @AfterEach
  void cleanUp() throws IOException {
    // Delete Data
    var deleteIndexRequest = new DeleteIndexRequest.Builder().index(documentIndexName).build();
    var deleteIndexResponse = esClient.indices().delete(deleteIndexRequest);
    assertTrue(deleteIndexResponse.acknowledged());
  }

  @AfterAll
  static void destroy() {
    elasticsearchContainer.stop();
  }

  @Test
  void uploadDocumentsTest() {
    String id = UUID.randomUUID().toString();
    var sampleDocument =
        buildDocumentInstance(
            id,
            LocalDateTime.now(Clock.systemUTC()),
            BYTE_CONTENT,
            IT_DEMO_USER,
            FILE_NAME,
            PNG_EXTENSION);
    var uploadedDocuments = esDocsManagerRepo.uploadDocuments(List.of(sampleDocument));

    assertNotNull(uploadedDocuments);

    assertThat(uploadedDocuments)
        .filteredOn(
            doc ->
                doc.id().equalsIgnoreCase(sampleDocument.id())
                    && doc.userId().equalsIgnoreCase(sampleDocument.userId())
                    && doc.fileName().equalsIgnoreCase(sampleDocument.fileName())
                    && doc.extension().equalsIgnoreCase(sampleDocument.extension()))
        .hasSize(1);

    byte[] documentContent = esDocsManagerRepo.getDocumentContent(id);

    assertNotNull(documentContent);
    assertArrayEquals(BYTE_CONTENT, documentContent);
  }

  @Test
  void getDocumentContentTest() {
    byte[] documentContent = esDocsManagerRepo.getDocumentContent(DOCUMENT_ID);

    assertNotNull(documentContent);
    assertArrayEquals(BYTE_CONTENT, documentContent);
  }

  @Test
  void deleteDocumentsTest() {
    var documentIds = Set.of(DOCUMENT_ID, SAMPLE_DOCUMENT_ID);
    esDocsManagerRepo.deleteDocuments(documentIds);

    documentIds.forEach(
        documentId ->
            assertThrows(
                NoSuchElementException.class,
                () -> esDocsManagerRepo.getDocumentContent(documentId)));
  }

  @Test
  void getContentOfUploadedDocumentsTest() {
    Set<String> documentIds = Set.of(DOCUMENT_ID, SAMPLE_DOCUMENT_ID);

    documentIds.forEach(
        documentId -> assertNotNull(esDocsManagerRepo.getDocumentContent(documentId)));
  }

  @Test
  void getAllDocumentsByUserIdTest() {
    Set<Document> allDocumentsByUserIdWithPNGExtension =
        esDocsManagerRepo.getAllDocumentsByUserId(
            IT_DEMO_USER,
            PNG_EXTENSION,
            LocalDateTime.now(Clock.systemUTC()).minusDays(1L),
            LocalDateTime.now(Clock.systemUTC()));

    assertNotNull(allDocumentsByUserIdWithPNGExtension);
    assertFalse(allDocumentsByUserIdWithPNGExtension.isEmpty());

    Set<Document> allDocumentsByUserIdWithPDFExtension =
        esDocsManagerRepo.getAllDocumentsByUserId(
            IT_DEMO_USER,
            PDF_CONTENT_TYPE,
            LocalDateTime.now(Clock.systemUTC()).minusDays(1L),
            LocalDateTime.now(Clock.systemUTC()));

    assertNotNull(allDocumentsByUserIdWithPDFExtension);
    assertTrue(allDocumentsByUserIdWithPDFExtension.isEmpty());

    Set<Document> allDocumentsByUserId =
        esDocsManagerRepo.getAllDocumentsByUserId(
            IT_DEMO_USER,
            null,
            LocalDateTime.now(Clock.systemUTC()).minusDays(1L),
            LocalDateTime.now(Clock.systemUTC()));

    assertNotNull(allDocumentsByUserId);
    assertEquals(allDocumentsByUserIdWithPNGExtension.size(), allDocumentsByUserId.size());
  }

  private List<Document> uploadDocuments(Document... docs) {
    return esDocsManagerRepo.uploadDocuments(Arrays.asList(docs));
  }
}
