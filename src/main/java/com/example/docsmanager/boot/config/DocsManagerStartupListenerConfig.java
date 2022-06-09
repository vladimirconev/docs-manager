package com.example.docsmanager.boot.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.example.docsmanager.boot.DocumentManagerStartupListener;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DocsManagerStartupListenerConfig {

  @Autowired
  private ElasticsearchClient esClient;

  @Value("${custom.document.index.name}")
  private String documentIndexName;

  @Bean
  public String documentIndexName() {
    return documentIndexName;
  }

  @Bean
  public DocumentManagerStartupListener documentManagerStartupListener() {
    return new DocumentManagerStartupListener(esClient, documentIndexName);
  }
}
