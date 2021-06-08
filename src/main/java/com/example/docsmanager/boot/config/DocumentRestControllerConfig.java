package com.example.docsmanager.boot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.docsmanager.adapter.in.DocumentRestController;
import com.example.docsmanager.domain.DocumentManagement;

/**
 * Configuration for {@link DocumentRestController}.
 * 
 * @author Vladimir.Conev
 *
 */

@Configuration
public class DocumentRestControllerConfig {

	@Bean
	public DocumentRestController documentRestController(
			final DocumentManagement docsManagement) {
		return new DocumentRestController(docsManagement);
	}

}
