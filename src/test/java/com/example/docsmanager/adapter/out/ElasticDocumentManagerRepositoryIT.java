package com.example.docsmanager.adapter.out;

import static org.junit.jupiter.api.Assertions.*;

import com.example.docsmanager.DocsElasticsearchContainer;
import com.example.docsmanager.TestObjectFactory;
import com.example.docsmanager.boot.DocsManagerApplication;
import com.example.docsmanager.domain.entity.Document;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
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
  private static final ElasticsearchContainer elasticsearchContainer = new DocsElasticsearchContainer();

  private static final String IT_DEMO_USER = "integration_test_demo_user";
  private static final String EXPLICIT_MAPPINGS_JSON_PATH = "/explicit_mappings.json";

  @Autowired
  private ElasticDocumentManagerRepository esDocsManagerRepo;

  @Autowired
  private RestHighLevelClient restHighLevelClient;

  @Autowired
  private String documentIndexName;

  @BeforeAll
  static void setUp() {
    elasticsearchContainer.start();
  }

  @BeforeEach
  void testIsContainerRunning() throws IOException {
    assertTrue(elasticsearchContainer.isRunning());
    // Create Index + Mappings
    boolean indexExists = restHighLevelClient
      .indices()
      .exists(new GetIndexRequest(documentIndexName), RequestOptions.DEFAULT);
    if (!indexExists) {
      CreateIndexRequest createIndexRequest = new CreateIndexRequest(documentIndexName);
      String mappings = IOUtils.resourceToString(
        EXPLICIT_MAPPINGS_JSON_PATH,
        StandardCharsets.UTF_8
      );
      createIndexRequest.mapping(mappings, XContentType.JSON);
      CreateIndexResponse createIndexResponse = restHighLevelClient
        .indices()
        .create(createIndexRequest, RequestOptions.DEFAULT);
      assertTrue(createIndexResponse.isAcknowledged());
    }
    // Load sample data
    List<Document> uploadDocuments = uploadDocuments(
      buildDocumentInstance(DOCUMENT_ID, LocalDateTime.now(), BYTE_CONTENT, IT_DEMO_USER),
      buildDocumentInstance(
        SAMPLE_DOCUMENT_ID,
        LocalDateTime.now(),
        BYTE_CONTENT,
        IT_DEMO_USER
      )
    );

    Set<String> documentIds = uploadDocuments
      .stream()
      .map(Document::id)
      .collect(Collectors.toSet());
    assertEquals(documentIds, Set.of(DOCUMENT_ID, SAMPLE_DOCUMENT_ID));
  }

  @AfterEach
  void cleanUp() throws IOException {
    //Delete Data
    var deleteIndexRequest = new DeleteIndexRequest().indices(documentIndexName);
    var deleteResponse = restHighLevelClient
      .indices()
      .delete(deleteIndexRequest, RequestOptions.DEFAULT);

    assertTrue(deleteResponse.isAcknowledged());
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
          () -> esDocsManagerRepo.getDocumentContent(documentId)
        )
    );
  }

  @Test
  void uploadMultipleDocumentsTest() {
    Set<String> documentIds = Set.of(DOCUMENT_ID, SAMPLE_DOCUMENT_ID);

    documentIds.forEach(
      documentId -> assertNotNull(esDocsManagerRepo.getDocumentContent(documentId))
    );
  }

  @Test
  void getAllDocumentsByUserIdTest() {
    Set<Document> allDocumentsByUserIdWithPNGExtension = esDocsManagerRepo.getAllDocumentsByUserId(
      IT_DEMO_USER,
      PNG_EXTENSION
    );

    assertNotNull(allDocumentsByUserIdWithPNGExtension);
    assertFalse(allDocumentsByUserIdWithPNGExtension.isEmpty());

    Set<Document> allDocumentsByUserIdWithPDFExtension = esDocsManagerRepo.getAllDocumentsByUserId(
      IT_DEMO_USER,
      PDF_CONTENT_TYPE
    );

    assertNotNull(allDocumentsByUserIdWithPDFExtension);
    assertTrue(allDocumentsByUserIdWithPDFExtension.isEmpty());

    Set<Document> allDocumentsByUserId = esDocsManagerRepo.getAllDocumentsByUserId(
      IT_DEMO_USER,
      null
    );

    assertNotNull(allDocumentsByUserId);
    assertEquals(
      allDocumentsByUserIdWithPNGExtension.size(),
      allDocumentsByUserId.size()
    );
  }

  private List<Document> uploadDocuments(Document... docs) {
    return esDocsManagerRepo.uploadDocuments(Arrays.asList(docs));
  }
}
