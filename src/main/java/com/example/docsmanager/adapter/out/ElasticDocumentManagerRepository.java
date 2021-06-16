package com.example.docsmanager.adapter.out;

import java.util.Arrays;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.example.docsmanager.adapter.out.db.dto.DocumentElasticDto;
import com.example.docsmanager.domain.DocumentManagementRepository;
import com.example.docsmanager.domain.entity.Document;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ElasticDocumentManagerRepository implements DocumentManagementRepository {
	
	private static final String USER_ID = "userId";
	private static final String EXTENSION = "extension";
	
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
	public Set<Document> getAllDocumentsByUserId(final String userId, final String extension) {
		Set<Document> documents= new HashSet<>();
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().filter(QueryBuilders.termQuery(USER_ID, userId));
		if (StringUtils.isNotBlank(extension)) {
			boolQueryBuilder.must(QueryBuilders.boolQuery().filter(QueryBuilders.termQuery(EXTENSION, extension)));
		}
		final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
		SearchRequest searchRequest = new SearchRequest(documentIndexName);
		searchRequest.scroll(scroll);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(boolQueryBuilder);
		searchRequest.source(searchSourceBuilder);

		SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT); 
		String scrollId = searchResponse.getScrollId();
		SearchHit[] searchHits = searchResponse.getHits().getHits();

		while (searchHits != null && searchHits.length > 0) {
			Set<Document> docs = Arrays.asList(searchHits).parallelStream().filter(Objects::nonNull).map(
					searchHit -> new ObjectMapper().convertValue(searchHit.getSourceAsMap(), DocumentElasticDto.class))
					.map(DocumentRepositoryMapper::mapDocumentElasticDtoToDocument).collect(Collectors.toSet());
			if(!docs.isEmpty()) {
				documents.addAll(docs);
			}
			SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
			scrollRequest.scroll(scroll);
			searchResponse = restHighLevelClient.scroll(scrollRequest, RequestOptions.DEFAULT);
			scrollId = searchResponse.getScrollId();
			searchHits = searchResponse.getHits().getHits();
		}

		ClearScrollRequest clearScrollRequest = new ClearScrollRequest(); 
		clearScrollRequest.addScrollId(scrollId);
		ClearScrollResponse clearScrollResponse = restHighLevelClient.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
		boolean succeeded = clearScrollResponse.isSucceeded();
		log.debug("Is scroll cleared out:{}.", succeeded);
		return documents;
	}

}
