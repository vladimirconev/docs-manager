package com.example.docsmanager.boot.config;

import com.example.docsmanager.adapter.in.DocumentRestController;
import com.example.docsmanager.domain.DocumentManagement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Configuration for {@link DocumentRestController}.
 *
 * @author Vladimir.Conev
 *
 */

@Configuration
@EnableOpenApi
public class DocumentRestControllerConfig {

  private static final String IN_ADAPTER_BASE_PKG = "com.example.docsmanager.adapter.in";

  @Bean
  public DocumentRestController documentRestController(
    final DocumentManagement docsManagement
  ) {
    return new DocumentRestController(docsManagement);
  }

  @Bean
  public Docket docket() {
    return new Docket(DocumentationType.OAS_30)
      .apiInfo(
        new ApiInfoBuilder()
          .title("Documents Manager API")
          .description("A CRUD API to demonstrate capabilities of Elasticsearch")
          .version("2.0.0-SNAPSHOT")
          .build()
      )
      .select()
      .apis(RequestHandlerSelectors.basePackage(IN_ADAPTER_BASE_PKG))
      .build();
  }
}
