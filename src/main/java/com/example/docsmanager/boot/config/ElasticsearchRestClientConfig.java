package com.example.docsmanager.boot.config;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

@Configuration
public class ElasticsearchRestClientConfig extends AbstractElasticsearchConfiguration {

  @Value("${spring.elasticsearch.uris:http://localhost:9200}")
  private String host;

  @Value("${spring.elasticsearch.username:elastic}")
  private String username;

  @Value("${spring.elasticsearch.password:change_me}")
  private String password;

  @Bean
  @Override
  public RestHighLevelClient elasticsearchClient() {
    final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    credentialsProvider.setCredentials(
      AuthScope.ANY,
      new UsernamePasswordCredentials(username, password)
    );
    try {
      var uri = new URI(host);
      var hostname = uri.getHost();
      var port = uri.getPort();
      var scheme = uri.getScheme();
      return new RestHighLevelClient(
        RestClient
          .builder(new HttpHost(hostname, port, scheme))
          .setHttpClientConfigCallback(
            hcb -> hcb.setDefaultCredentialsProvider(credentialsProvider)
          )
      );
    } catch (URISyntaxException ex) {
      throw new RuntimeException(ex);
    }
  }
}
