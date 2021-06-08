package com.example.docsmanager.boot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.docsmanager.domain.DocumentManagement;
import com.example.docsmanager.domain.DocumentManagementRepository;
import com.example.docsmanager.domain.DocumentManager;

@Configuration
public class DocumentManagementConfig {
	
	
	@Bean
	public DocumentManagement documentManagement(final DocumentManagementRepository repo) {
		return new DocumentManager(repo);
	}

}
