package com.example.docsmanager.boot.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.example.docsmanager.adapter.out.DocumentElasticRepository;
import com.example.docsmanager.adapter.out.ElasticDocumentManagerRepository;
import com.example.docsmanager.domain.DocumentManagementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackageClasses = DocumentElasticRepository.class)
public class DocumentManagementRepositoryConfig {

  @Autowired
  private ElasticsearchClient esClient;

  @Value("${custom.document.index.name}")
  private String documentIndexName;

  @Bean
  public DocumentManagementRepository documentManagementRepository(
    final DocumentElasticRepository documentElasticRepo
  ) {
    return new ElasticDocumentManagerRepository(
      documentElasticRepo,
      documentIndexName,
      esClient
    );
  }
}
