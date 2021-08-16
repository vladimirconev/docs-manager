package com.example.docsmanager;

import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

public class DocsElasticsearchContainer extends ElasticsearchContainer {

  private static final String DOCKER_IMAGE_FULL_NAME_ELASTICSEARCH =
    "docker.elastic.co/elasticsearch/elasticsearch:7.13.4";

  public DocsElasticsearchContainer() {
    super(DockerImageName.parse(DOCKER_IMAGE_FULL_NAME_ELASTICSEARCH));
    this.addFixedExposedPort(9200, 9200);
    this.addFixedExposedPort(9300, 9300);
  }
}
