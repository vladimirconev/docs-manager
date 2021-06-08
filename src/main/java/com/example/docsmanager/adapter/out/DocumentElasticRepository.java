package com.example.docsmanager.adapter.out;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.example.docsmanager.adapter.out.db.dto.DocumentElasticDto;

public interface DocumentElasticRepository extends ElasticsearchRepository<DocumentElasticDto, String> {

}
