package com.example.docsmanager.boot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.docsmanager.adapter.in.DocumentRestController;
import com.example.docsmanager.domain.DocumentManagement;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Configuration for {@link DocumentRestController}.
 * 
 * @author Vladimir.Conev
 *
 */

@Configuration
@EnableSwagger2
public class DocumentRestControllerConfig {

	private static final String IN_ADAPTER_BASE_PKG = 
			"com.example.docsmanager.adapter.in";
	
	@Bean
	public DocumentRestController documentRestController(
			final DocumentManagement docsManagement) {
		return new DocumentRestController(docsManagement);
	}
	
	@Bean
	public Docket productApi() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage(IN_ADAPTER_BASE_PKG)).build();
	}

}
