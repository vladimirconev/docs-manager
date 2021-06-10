package com.example.docsmanager.adapter.out;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.example.docsmanager.adapter.out.db.dto.DocumentElasticDto;
import com.example.docsmanager.domain.DocumentManagementRepository;
import com.example.docsmanager.domain.entity.Document;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class ElasticDocumentManagerRepository implements DocumentManagementRepository {
	
	private static final String USER_ID = "userId";
	private static final int MAX_SIZE = 10000;
	
	private final DocumentElasticRepository documentElasticRepository;
	private final RestHighLevelClient restHighLevelClient;
	private final String documentIndexName;
	
	
	@Override
	public Document uploadDocument(final Document document) {
		var dto = documentElasticRepository.save(DocumentRepositoryMapper.mapDocumentToDocumentElasticDto(document));
		return DocumentRepositoryMapper.mapDocumentElasticDtoToDocument(dto);
	}

	@Override
	public byte[] getDocumentContent(final String id) {
		DocumentElasticDto documentElasticDto = documentElasticRepository.findById(id)
				.orElseThrow(NoSuchElementException::new);
		return documentElasticDto.getContent();
	}

	@Override
	public void deleteDocuments(final Set<String> documentIds) {
		documentElasticRepository.deleteAllById(documentIds);
	}
	
	@SneakyThrows
	@Override
	public Set<Document> getAllDocumentsByUserId(final String userId) {
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().filter(QueryBuilders.termQuery(USER_ID, userId));
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().query(boolQueryBuilder).size(MAX_SIZE);
		SearchRequest searchRequest = new SearchRequest(documentIndexName).source(searchSourceBuilder);

		SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		SearchHit[] hits = searchResponse.getHits().getHits();
		Set<Document> documents = Arrays.asList(hits).parallelStream()
				.filter(Objects::nonNull)
				.map(searchHit -> new ObjectMapper().convertValue(searchHit.getSourceAsMap(), Document.class))
				.collect(Collectors.toSet());

		return documents;
	}

}
