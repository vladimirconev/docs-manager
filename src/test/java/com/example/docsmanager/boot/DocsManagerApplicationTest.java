package com.example.docsmanager.boot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.docsmanager.DocsElasticsearchContainer;
import com.example.docsmanager.adapter.in.DocumentRestController;
import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

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
}
