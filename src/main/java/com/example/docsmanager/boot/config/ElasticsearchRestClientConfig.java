package com.example.docsmanager.boot.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchRestClientConfig {

  @Value("${spring.elasticsearch.uris:http://localhost:9200}")
  private String host;

  @Value("${spring.elasticsearch.username:elastic}")
  private String username;

  @Value("${spring.elasticsearch.password:change_me}")
  private String password;

  @Bean
  public ElasticsearchClient esClient() {
    final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    credentialsProvider.setCredentials(
        AuthScope.ANY, new UsernamePasswordCredentials(username, password));
    try {
      var uri = new URI(host);
      var hostname = uri.getHost();
      var port = uri.getPort();
      var scheme = uri.getScheme();
      RestClient httpClient =
          RestClient.builder(new HttpHost(hostname, port, scheme))
              .setHttpClientConfigCallback(
                  hcb -> hcb.setDefaultCredentialsProvider(credentialsProvider))
              .build();
      ElasticsearchTransport transport =
          new RestClientTransport(httpClient, new JacksonJsonpMapper());

      return new ElasticsearchClient(transport);
    } catch (URISyntaxException ex) {
      throw new RuntimeException(ex);
    }
  }
}
