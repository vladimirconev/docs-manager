package com.example.docsmanager.adapter.out;

import com.example.docsmanager.adapter.out.db.dto.DocumentElasticDto;
import com.example.docsmanager.domain.DocumentManagementRepository;
import com.example.docsmanager.domain.entity.Document;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.cache.annotation.Cacheable;

@Slf4j
@RequiredArgsConstructor
public class ElasticDocumentManagerRepository implements DocumentManagementRepository {

  private static final String USER_ID = "userId";
  private static final String EXTENSION = "extension";
  private static final String[] INCLUDED_SOURCE_FIELDS = {
    "creationDate",
    EXTENSION,
    USER_ID,
    "id",
    "fileName",
  };
  private static final String[] EXCLUDED_SOURCE_FIELDS = { "content" };

  private final DocumentElasticRepository documentElasticRepository;
  private final RestHighLevelClient restHighLevelClient;
  private final String documentIndexName;

  @Override
  public Document uploadDocument(final Document document) {
    var dto = documentElasticRepository.save(
      DocumentRepositoryMapper.mapDocumentToDocumentElasticDto(document)
    );
    return DocumentRepositoryMapper.mapDocumentElasticDtoToDocument(dto);
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
  public void deleteDocuments(final Set<String> documentIds) {
    documentElasticRepository.deleteAllById(documentIds);
  }

  @SneakyThrows
  @Override
  public Set<Document> getAllDocumentsByUserId(
    final String userId,
    final String extension
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
    final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
    SearchRequest searchRequest = new SearchRequest(documentIndexName);
    searchRequest.scroll(scroll);
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.query(boolQueryBuilder);
    searchSourceBuilder.fetchSource(INCLUDED_SOURCE_FIELDS, EXCLUDED_SOURCE_FIELDS);
    searchRequest.source(searchSourceBuilder);

    ObjectMapper objectMapper = new ObjectMapper();

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
        .map(
          (var sourceMap) ->
            objectMapper.convertValue(sourceMap, DocumentElasticDto.class)
        )
        .map(DocumentRepositoryMapper::mapDocumentElasticDtoToDocument)
        .collect(Collectors.toSet());
      if (!docs.isEmpty()) {
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
    ClearScrollResponse clearScrollResponse = restHighLevelClient.clearScroll(
      clearScrollRequest,
      RequestOptions.DEFAULT
    );
    boolean succeeded = clearScrollResponse.isSucceeded();
    log.debug("Is scroll cleared out: {}.", succeeded);
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
