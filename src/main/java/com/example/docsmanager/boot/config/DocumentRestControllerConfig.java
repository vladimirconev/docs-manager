package com.example.docsmanager.boot.config;

import com.example.docsmanager.adapter.in.DocumentRestController;
import com.example.docsmanager.domain.DocumentManagement;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    final DocumentManagement docsManagement
  ) {
    return new DocumentRestController(docsManagement);
  }

  @Bean
  public OpenAPI documentsManagerOpenAPI() {
    return new OpenAPI()
      .info(
        new Info()
          .title("Documents Manager API")
          .description("A CRUD API to demonstrate capabilities of Elasticsearch")
          .version("v2.0.1-SNAPSHOT")
      );
  }
}
