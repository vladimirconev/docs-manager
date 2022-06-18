package com.example.docsmanager.adapter.out;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Time;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.ScrollRequest;
import co.elastic.clients.elasticsearch.core.ScrollResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.example.docsmanager.adapter.out.db.dto.DocumentElasticDto;
import com.example.docsmanager.domain.DocumentManagementRepository;
import com.example.docsmanager.domain.entity.Document;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

public class ElasticDocumentManagerRepository implements DocumentManagementRepository {

  final Logger logger = LoggerFactory.getLogger(ElasticDocumentManagerRepository.class);

  private static final String USER_ID = "userId";
  private static final String EXTENSION = "extension";
  private static final String CREATION_DATE = "creationDate";
  private static final String ID = "id";
  private static final String FILE_NAME = "fileName";
  private static final String CONTENT = "content";

  private final DocumentElasticRepository documentElasticRepository;
  private final String documentIndexName;
  private final ElasticsearchClient esClient;

  public ElasticDocumentManagerRepository(
      final DocumentElasticRepository documentElasticRepository,
      final String index,
      final ElasticsearchClient esClient) {
    this.documentElasticRepository = documentElasticRepository;
    this.documentIndexName = index;
    this.esClient = esClient;
  }

  @Cacheable(cacheNames = "docs_byte_content")
  @Override
  public byte[] getDocumentContent(final String id) {
    var documentElasticDto =
        documentElasticRepository.findById(id).orElseThrow(NoSuchElementException::new);
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
      final LocalDateTime to) {
    Set<Document> documents = new HashSet<>();
    BoolQuery.Builder boolQueryBuilder = getBoolQueryBuilder(userId, extension, from, to);
    try {
      var time = new Time.Builder().time("1m").build();
      var request =
          co.elastic.clients.elasticsearch.core.SearchRequest.of(
              s ->
                  s.index(documentIndexName)
                      .source(
                          src ->
                              src.filter(
                                  f ->
                                      f.excludes(List.of(CONTENT))
                                          .includes(
                                              List.of(
                                                  CREATION_DATE,
                                                  EXTENSION,
                                                  USER_ID,
                                                  ID,
                                                  FILE_NAME))))
                      .query(boolQueryBuilder.build()._toQuery())
                      .scroll(time));
      co.elastic.clients.elasticsearch.core.SearchResponse<DocumentElasticDto> searchResponse =
          esClient.search(request, DocumentElasticDto.class);
      var scrollId = searchResponse.scrollId();
      List<Hit<DocumentElasticDto>> hits = searchResponse.hits().hits();
      while (hits != null && !hits.isEmpty()) {
        Set<Document> docs =
            hits.stream()
                .map(Hit::source)
                .filter(Objects::nonNull)
                .map(DocumentRepositoryMapper::mapDocumentElasticDtoToDocument)
                .collect(Collectors.toSet());
        if (!docs.isEmpty()) {
          documents.addAll(docs);
        }
        ScrollRequest scrollRequest =
            new ScrollRequest.Builder().scrollId(scrollId).scroll(time).build();
        ScrollResponse<DocumentElasticDto> scrollResponse =
            esClient.scroll(scrollRequest, DocumentElasticDto.class);
        scrollId = scrollResponse.scrollId();
        hits = scrollResponse.hits().hits();
      }

      co.elastic.clients.elasticsearch.core.ClearScrollRequest clearScrollRequest =
          new co.elastic.clients.elasticsearch.core.ClearScrollRequest.Builder()
              .scrollId(List.of(Objects.requireNonNull(scrollId)))
              .build();
      co.elastic.clients.elasticsearch.core.ClearScrollResponse clear_scroll_response =
          esClient.clearScroll(clearScrollRequest);

      logger.debug("Is scroll cleared out: {}.", clear_scroll_response.succeeded());
    } catch (IOException ioException) {
      logger.error(
          String.format(
              "Error on fetching all documents by user id %s and extension %s.", userId, extension),
          ioException);
    }
    return documents;
  }

  @NotNull
  private BoolQuery.Builder getBoolQueryBuilder(
      String userId, String extension, LocalDateTime from, LocalDateTime to) {
    Query byUserId = TermQuery.of(t -> t.field(USER_ID).value(userId))._toQuery();
    BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder().filter(byUserId);
    if (StringUtils.isNotBlank(extension)) {
      boolQueryBuilder.must(TermQuery.of(t -> t.field(EXTENSION).value(extension))._toQuery());
    }
    if (from != null || to != null) {
      RangeQuery.Builder rangeQueryBuilder = new RangeQuery.Builder().field(CREATION_DATE);
      if (from != null) {
        rangeQueryBuilder.from(from.format(DateTimeFormatter.ISO_DATE_TIME));
      }
      if (to != null) {
        rangeQueryBuilder.to(to.format(DateTimeFormatter.ISO_DATE_TIME));
      }
      boolQueryBuilder.must(rangeQueryBuilder.build()._toQuery());
    }
    return boolQueryBuilder;
  }

  @Override
  public List<Document> uploadDocuments(final List<Document> documents) {
    Iterable<DocumentElasticDto> dtos =
        documentElasticRepository.saveAll(
            DocumentRepositoryMapper.mapDocumentsToDocumentElasticDtos(documents));
    return StreamSupport.stream(dtos.spliterator(), false)
        .map(DocumentRepositoryMapper::mapDocumentElasticDtoToDocument)
        .collect(Collectors.toList());
  }
}
