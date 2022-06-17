package com.example.docsmanager.boot;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class DocumentManagerStartupListener implements ApplicationListener<ContextRefreshedEvent> {

  private static final String EXPLICIT_MAPPINGS_JSON_PATH = "/explicit_mappings.json";

  final Logger logger = LoggerFactory.getLogger(DocumentManagerStartupListener.class);

  private final ElasticsearchClient esClient;
  private final String indexName;

  public DocumentManagerStartupListener(ElasticsearchClient esClient, String indexName) {
    this.esClient = esClient;
    this.indexName = indexName;
  }

  @SuppressWarnings("NullableProblems")
  @Override
  public void onApplicationEvent(final ContextRefreshedEvent event) {
    try {
      createIndex();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /** Creates an index if not exist and applies Explicit mappings. */
  private void createIndex() throws IOException {
    BooleanResponse booleanResponse = esClient.indices().exists(e -> e.index(indexName));
    if (!booleanResponse.value()) {
      var inputStream = this.getClass().getResourceAsStream(EXPLICIT_MAPPINGS_JSON_PATH);
      var createIndexResponse =
          esClient.indices().create(c -> c.index(indexName).mappings(m -> m.withJson(inputStream)));
      logger.info(
          "Creation of Index {} is acknowledged:{}", indexName, createIndexResponse.acknowledged());
    }
  }
}
