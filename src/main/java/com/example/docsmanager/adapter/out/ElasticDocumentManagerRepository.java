package com.example.docsmanager.adapter.out;

import com.example.docsmanager.adapter.out.db.dto.DocumentElasticDto;
import com.example.docsmanager.domain.DocumentManagementRepository;
import com.example.docsmanager.domain.entity.Document;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

public class ElasticDocumentManagerRepository implements DocumentManagementRepository {

  final Logger logger = LoggerFactory.getLogger(ElasticDocumentManagerRepository.class);

  private static final String USER_ID = "userId";
  private static final String EXTENSION = "extension";
  private static final String CREATION_DATE = "creationDate";
  private static final String[] INCLUDED_SOURCE_FIELDS = {
    CREATION_DATE,
    EXTENSION,
    USER_ID,
    "id",
    "fileName",
  };
  private static final String[] EXCLUDED_SOURCE_FIELDS = { "content" };

  private final DocumentElasticRepository documentElasticRepository;
  private final RestHighLevelClient restHighLevelClient;
  private final String documentIndexName;

  public ElasticDocumentManagerRepository(
    final DocumentElasticRepository documentElasticRepository,
    final RestHighLevelClient restHighLevelClient,
    final String index
  ) {
    this.documentElasticRepository = documentElasticRepository;
    this.restHighLevelClient = restHighLevelClient;
    this.documentIndexName = index;
  }

  @Cacheable(cacheNames = "docs_byte_content")
  @Override
  public byte[] getDocumentContent(final String id) {
    var documentElasticDto = documentElasticRepository
      .findById(id)
      .orElseThrow(NoSuchElementException::new);
    return Base64.getDecoder().decode(documentElasticDto.content());
  }

  @Override
  @CacheEvict(cacheNames = "docs_byte_content", allEntries = true)
  public void deleteDocuments(final Set<String> documentIds) {
    documentElasticRepository.deleteAllById(documentIds);
  }

  @Override
  public Set<Document> getAllDocumentsByUserId(
    final String userId,
    final String extension,
    final LocalDateTime from,
    final LocalDateTime to
  ) {
    Set<Document> documents = new HashSet<>();
    BoolQueryBuilder boolQueryBuilder = QueryBuilders
      .boolQuery()
      .filter(QueryBuilders.termQuery(USER_ID, userId));
    if (StringUtils.isNotBlank(extension)) {
      boolQueryBuilder.must(
        QueryBuilders.boolQuery().filter(QueryBuilders.termQuery(EXTENSION, extension))
      );
    }
    if (from != null || to != null) {
      RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(CREATION_DATE);
      if (from != null) {
        rangeQueryBuilder.from(from, true);
      }
      if (to != null) {
        rangeQueryBuilder.to(to, true);
      }
      boolQueryBuilder.must(rangeQueryBuilder);
    }

    final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
    SearchRequest searchRequest = new SearchRequest(documentIndexName);
    searchRequest.scroll(scroll);
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.query(boolQueryBuilder);
    searchSourceBuilder.fetchSource(INCLUDED_SOURCE_FIELDS, EXCLUDED_SOURCE_FIELDS);
    searchRequest.source(searchSourceBuilder);

    ObjectMapper objectMapper = new ObjectMapper();

    try {
      SearchResponse searchResponse = restHighLevelClient.search(
        searchRequest,
        RequestOptions.DEFAULT
      );
      String scrollId = searchResponse.getScrollId();
      SearchHit[] searchHits = searchResponse.getHits().getHits();

      while (searchHits != null && searchHits.length > 0) {
        Set<Document> docs = Arrays
          .stream(searchHits)
          .filter(Objects::nonNull)
          .map(SearchHit::getSourceAsMap)
          .map((var sourceMap) ->
            objectMapper.convertValue(sourceMap, DocumentElasticDto.class)
          )
          .map(DocumentRepositoryMapper::mapDocumentElasticDtoToDocument)
          .collect(Collectors.toSet());
        if (!docs.isEmpty()) {
          documents.addAll(docs);
        }
        SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
        scrollRequest.scroll(scroll);
        searchResponse =
          restHighLevelClient.scroll(scrollRequest, RequestOptions.DEFAULT);
        scrollId = searchResponse.getScrollId();
        searchHits = searchResponse.getHits().getHits();
      }

      ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
      clearScrollRequest.addScrollId(scrollId);
      ClearScrollResponse clearScrollResponse = restHighLevelClient.clearScroll(
        clearScrollRequest,
        RequestOptions.DEFAULT
      );
      boolean succeeded = clearScrollResponse.isSucceeded();
      logger.debug("Is scroll cleared out: {}.", succeeded);
    } catch (IOException ioException) {
      logger.error(
        String.format(
          "Error on fetching all documents by user id %s and extension %s.",
          userId,
          extension
        ),
        ioException
      );
    }
    return documents;
  }

  @Override
  public List<Document> uploadDocuments(final List<Document> documents) {
    Iterable<DocumentElasticDto> dtos = documentElasticRepository.saveAll(
      DocumentRepositoryMapper.mapDocumentsToDocumentElasticDtos(documents)
    );
    return StreamSupport
      .stream(dtos.spliterator(), false)
      .map(DocumentRepositoryMapper::mapDocumentElasticDtoToDocument)
      .collect(Collectors.toList());
  }
}
