package com.example.docsmanager.adapter.out;

import com.example.docsmanager.adapter.out.db.dto.DocumentElasticDto;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface DocumentElasticRepository
    extends ElasticsearchRepository<DocumentElasticDto, String> {}
