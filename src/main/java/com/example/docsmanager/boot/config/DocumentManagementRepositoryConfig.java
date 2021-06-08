package com.example.docsmanager.boot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import com.example.docsmanager.adapter.out.DocumentElasticRepository;
import com.example.docsmanager.adapter.out.ElasticDocumentManagerRepository;
import com.example.docsmanager.domain.DocumentManagementRepository;

@Configuration
@EnableElasticsearchRepositories(basePackageClasses = DocumentElasticRepository.class)
public class DocumentManagementRepositoryConfig {
	
	@Bean
	public DocumentManagementRepository documentManagementRepository(
			final DocumentElasticRepository documentElasticRepo) {
		return new ElasticDocumentManagerRepository(documentElasticRepo);

	}

}
