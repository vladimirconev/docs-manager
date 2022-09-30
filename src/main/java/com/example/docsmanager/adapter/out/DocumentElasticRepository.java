package com.example.docsmanager.adapter.out;

import com.example.docsmanager.adapter.out.db.dto.DocumentElastic;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface DocumentElasticRepository
    extends ElasticsearchRepository<DocumentElastic, String> {}
