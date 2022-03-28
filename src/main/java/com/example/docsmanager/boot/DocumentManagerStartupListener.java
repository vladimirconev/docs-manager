package com.example.docsmanager.boot;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class DocumentManagerStartupListener
  implements ApplicationListener<ContextRefreshedEvent> {

  final Logger logger = LoggerFactory.getLogger(DocumentManagerStartupListener.class);

  private final RestHighLevelClient restHighLevelClient;
  private final String indexName;
  private final String explicitIndexMappings;

  public DocumentManagerStartupListener(
    RestHighLevelClient restHighLevelClient,
    String indexName,
    String explicitIndexMappings
  ) {
    this.restHighLevelClient = restHighLevelClient;
    this.indexName = indexName;
    this.explicitIndexMappings = explicitIndexMappings;
  }

  @Override
  public void onApplicationEvent(final ContextRefreshedEvent event) {
    createIndex();
  }

  /**
   * Creates an index if not exist and applies Explicit mappings.
   */
  private void createIndex() {
    try {
      boolean indexExists = restHighLevelClient
        .indices()
        .exists(new GetIndexRequest(indexName), RequestOptions.DEFAULT);
      if (!indexExists) {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
        createIndexRequest.mapping(explicitIndexMappings, XContentType.JSON);
        CreateIndexResponse createIndexResponse = restHighLevelClient
          .indices()
          .create(createIndexRequest, RequestOptions.DEFAULT);
        logger.info(
          "Creation of Index {} is acknowledged:{}",
          indexName,
          createIndexResponse.isAcknowledged()
        );
      }
    } catch (Exception exception) {
      logger.error(
        "Error on creating index and apply Explicit mappings due to:{}.",
        exception.getMessage()
      );
    }
  }
}
