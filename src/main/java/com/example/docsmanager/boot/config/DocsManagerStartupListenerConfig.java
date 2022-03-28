package com.example.docsmanager.boot.config;

import com.example.docsmanager.boot.DocumentManagerStartupListener;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DocsManagerStartupListenerConfig {

  private static final String EXPLICIT_MAPPINGS_JSON_PATH = "/explicit_mappings.json";

  @Autowired
  private RestHighLevelClient restHighLevelClient;

  @Value("${custom.document.index.name}")
  private String documentIndexName;

  @Bean
  public String documentIndexName() {
    return documentIndexName;
  }

  @Bean
  public DocumentManagerStartupListener documentManagerStartupListener()
    throws IOException {
    return new DocumentManagerStartupListener(
      restHighLevelClient,
      documentIndexName,
      IOUtils.resourceToString(EXPLICIT_MAPPINGS_JSON_PATH, StandardCharsets.UTF_8)
    );
  }
}
