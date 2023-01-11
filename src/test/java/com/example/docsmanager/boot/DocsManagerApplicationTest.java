package com.example.docsmanager.boot;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;

import com.example.docsmanager.DocsElasticsearchContainer;
import com.example.docsmanager.adapter.in.DocumentRestController;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.HttpStatus;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
public class DocsManagerApplicationTest {

  private static final ElasticsearchContainer elasticsearchContainer =
      new DocsElasticsearchContainer();

  @Autowired private DocumentRestController documentRestController;

  @Autowired private ServletWebServerApplicationContext webServerAppCtxt;

  @BeforeAll
  static void setUp() {
    elasticsearchContainer.start();
  }

  @AfterAll
  static void tearDown() {
    elasticsearchContainer.stop();
    assertThat(elasticsearchContainer.isRunning()).isFalse();
  }

  @BeforeEach
  void testIsContainerRunning() {
    assertThat(elasticsearchContainer.isRunning()).isTrue();
  }

  @Test
  void contextLoad() {
    assertThat(documentRestController).isNotNull();
  }

  @Test
  void getDocumentContentShouldReturnNotFoundForNonExistingId() {
    given()
        .when()
        .port(webServerAppCtxt.getWebServer().getPort())
        .get(String.format("api/v2/documents/%s", UUID.randomUUID()))
        .then()
        .assertThat()
        .statusCode(is(HttpStatus.NOT_FOUND.value()));
  }

  @Test
  void deleteDocumentsShouldReturnNoContent() {
    given()
        .when()
        .port(webServerAppCtxt.getWebServer().getPort())
        .delete(String.format("api/v2/documents?documentIds=%s", UUID.randomUUID()))
        .then()
        .assertThat()
        .statusCode(is(HttpStatus.NO_CONTENT.value()));
  }

  @Test
  void getDocumentsByUserIdShouldReturnOKWhenNoDocumentsFoundForUserId() {
    given()
        .when()
        .port(webServerAppCtxt.getWebServer().getPort())
        .get(String.format("api/v2/documents?userId=%s", RandomStringUtils.random(5)))
        .then()
        .assertThat()
        .statusCode(is(HttpStatus.OK.value()));
  }

  @Test
  void noSuchElementExceptionOnGetDocumentContent() {
    assertThrows(
        NoSuchElementException.class,
        () -> documentRestController.getDocumentContent(UUID.randomUUID().toString()));
  }

  @Test
  void emptySetOnGetDocumentsByNonExistingUserId() {
    var responseEntity =
        documentRestController.getDocumentsByUserId("USER__TEST", null, null, null);
    assertNotNull(responseEntity);
    assertNotNull(responseEntity.getBody());
    assertTrue(responseEntity.getBody().isEmpty());
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  @Test
  void noContentOnDeleteDocuments() {
    var responseEntity =
        documentRestController.deleteDocuments(Set.of(UUID.randomUUID().toString()));
    assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
  }
}
