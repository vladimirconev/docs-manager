package com.example.docsmanager.boot.config;

import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.example.docsmanager.boot.DocumentManagerStartupListener;

import lombok.SneakyThrows;

@Configuration
public class DocsManagerStartupListenerConfig {
	
	@Autowired
	private RestHighLevelClient restHighLevelClient;
	
	@Value("${custom.document.index.name}")
	private String documentIndexName;
	
	@Value("classpath:explicit_mappings.json")
	private Resource explicitMappingsResource;
	
	@Bean
	public String documentIndexName() {
		return documentIndexName;
	}
	
	@SneakyThrows
	@Bean
	public DocumentManagerStartupListener documentManagerStartupListener() {
		return new DocumentManagerStartupListener(restHighLevelClient, 
				documentIndexName, 
				FileUtils.readFileToString(explicitMappingsResource.getFile(), StandardCharsets.UTF_8));
	}

}
