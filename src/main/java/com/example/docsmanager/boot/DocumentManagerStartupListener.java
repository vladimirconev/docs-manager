package com.example.docsmanager.boot;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class DocumentManagerStartupListener implements ApplicationListener<ContextRefreshedEvent> {
	
	private final RestHighLevelClient restHighLevelClient;
	private final String indexName;
	private final String explicitIndexMappings;
	
	
	@Override
	public void onApplicationEvent(final ContextRefreshedEvent event) {
		try {
			boolean indexExists = restHighLevelClient.indices().exists(new GetIndexRequest(indexName),
					RequestOptions.DEFAULT);
			if (!indexExists) {
				CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
				createIndexRequest.mapping(explicitIndexMappings, XContentType.JSON);
				CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(createIndexRequest,
						RequestOptions.DEFAULT);
				log.info("Creation of Index {} is acknowledged:{}", indexName, createIndexResponse.isAcknowledged());
			}
		} catch (Exception exception) {
			log.error("Error on creating index and apply Explicit mappings due to:{}.", exception.getMessage());
		}

	}

}
