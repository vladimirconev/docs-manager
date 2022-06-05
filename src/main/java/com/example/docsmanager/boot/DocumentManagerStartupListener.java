package com.example.docsmanager.boot;

import java.io.IOException;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.xcontent.XContentType;
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

  @SuppressWarnings("NullableProblems")
  @Override
  public void onApplicationEvent(final ContextRefreshedEvent event) {
    try {
      createIndex();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * Creates an index if not exist and applies Explicit mappings.
   *
   */
  private void createIndex() throws IOException {
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
  }
}
