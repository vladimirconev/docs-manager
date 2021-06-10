package com.example.docsmanager.boot.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import com.example.docsmanager.adapter.out.DocumentElasticRepository;
import com.example.docsmanager.adapter.out.ElasticDocumentManagerRepository;
import com.example.docsmanager.domain.DocumentManagementRepository;

@Configuration
@EnableElasticsearchRepositories(basePackageClasses = DocumentElasticRepository.class)
public class DocumentManagementRepositoryConfig {
	

	@Autowired
	private RestHighLevelClient restHighLevelClient;
	
	@Value("${custom.document.index.name}")
	private String documentIndexName;
	
	@Bean
	public DocumentManagementRepository documentManagementRepository(
			final DocumentElasticRepository documentElasticRepo) {
		return new ElasticDocumentManagerRepository(documentElasticRepo,
				restHighLevelClient, documentIndexName);

	}

}
