package com.example.docsmanager.boot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.example.docsmanager.DocsElasticsearchContainer;
import com.example.docsmanager.adapter.in.DocumentRestController;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.NoSuchElementException;
import java.util.UUID;

@SpringBootTest
@Testcontainers
public class DocsManagerApplicationTest {

  @Container
  private static final ElasticsearchContainer elasticsearchContainer = new DocsElasticsearchContainer();

  @Autowired
  private DocumentRestController documentRestController;

  @BeforeAll
  static void setUp() {
    elasticsearchContainer.start();
  }

  @BeforeEach
  void testIsContainerRunning() {
    assertTrue(elasticsearchContainer.isRunning());
  }

  @Test
  void contextLoad() {
    assertThat(documentRestController).isNotNull();
  }

  @Test
  void noSuchElementExceptionOnGetDocumentContent(){
    assertThrows(NoSuchElementException.class, () -> documentRestController.getDocumentContent(UUID.randomUUID().toString()));
  }
  @Test
  void emptySetOnGetDocumentsByUserId(){
    var output = documentRestController.getDocumentsByUserId("USER__TEST",null,null,null);
    assertNotNull(output);
    assertNotNull(output.getBody());
    assertTrue(output.getBody().isEmpty());
    assertEquals(HttpStatus.OK,output.getStatusCode());
  }
}
