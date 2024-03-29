package com.example.docsmanager.adapter.in;

import static java.util.stream.Collectors.toSet;
import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;
import static org.springframework.http.MediaType.*;

import com.example.docsmanager.adapter.in.dto.DocumentMetadataResponse;
import com.example.docsmanager.adapter.in.dto.ErrorResponse;
import com.example.docsmanager.domain.DocumentManagement;
import com.example.docsmanager.domain.entity.Document;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Tag(name = "Documents")
@RequestMapping(
    value = {
      "#{'${enable.api.versioning:true}' ? '/api/v' + '${application.version}'.split('\\.')[0]:'' }",
    })
public class DocumentRestController {

  final Logger logger = LoggerFactory.getLogger(DocumentRestController.class);

  private final DocumentManagement documentManagement;

  public DocumentRestController(final DocumentManagement documentManagement) {
    this.documentManagement = documentManagement;
  }

  @Operation(
      summary = "Retrieve a document by ID",
      tags = {"Documents"})
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(
            responseCode = "503",
            description = "Service temporally unavailable",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Not Found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      })
  @GetMapping(path = "/documents/{documentId}", produces = APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<byte[]> getDocumentContent(
      final @PathVariable("documentId") String documentId) {
    logger.info("Fetching Document Data content with id:{}.", documentId);
    byte[] content = documentManagement.getDocumentContent(documentId);
    return new ResponseEntity<>(content, HttpStatus.OK);
  }

  @Operation(
      summary = "Upload documents",
      tags = {"Documents"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "CREATED",
            content =
                @Content(
                    array =
                        @ArraySchema(
                            schema = @Schema(implementation = DocumentMetadataResponse.class)))),
        @ApiResponse(
            responseCode = "503",
            description = "Service temporally unavailable",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      })
  @PostMapping(
      path = "/documents",
      produces = APPLICATION_JSON_VALUE,
      consumes = MULTIPART_FORM_DATA_VALUE)
  @SuppressWarnings("SameParameterValue")
  public ResponseEntity<List<DocumentMetadataResponse>> uploadDocuments(
      final @RequestPart("files") MultipartFile[] files,
      final @RequestParam("userId") String userId) {
    logger.info("Start of Uploading documents for user: {}.", userId);
    List<Document> documents =
        DocumentRestMapper.mapMultipartFilesToDocuments(List.of(files), userId);
    List<Document> uploadedDocuments = documentManagement.uploadDocuments(documents);
    return new ResponseEntity<>(
        DocumentRestMapper.mapDocumentsToDocumentMetadataResponseDtos(uploadedDocuments),
        HttpStatus.CREATED);
  }

  @Operation(
      description = "Delete a document by ID",
      tags = {"Documents"})
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "NO_CONTENT"),
        @ApiResponse(
            responseCode = "503",
            description = "Service temporally unavailable",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      })
  @DeleteMapping(path = "/documents")
  public ResponseEntity<Void> deleteDocuments(
      final @RequestParam("documentIds") Set<String> documentIds) {
    logger.info("Deleting documents with following IDs:{}.", documentIds);
    documentManagement.deleteDocuments(documentIds);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Operation(
      description =
          "Retrieve a document by user id. Optionally filtered by extension and/or creation date range.",
      tags = {"Documents"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content =
                @Content(
                    array =
                        @ArraySchema(
                            schema = @Schema(implementation = DocumentMetadataResponse.class)))),
        @ApiResponse(
            responseCode = "503",
            description = "Service temporally unavailable",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      })
  @GetMapping(path = "/documents", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<Set<DocumentMetadataResponse>> getDocumentsByUserId(
      final @RequestParam("userId") String userId,
      final @RequestParam(name = "extension", required = false) String extension,
      final @RequestParam(name = "from", required = false) @DateTimeFormat(iso = DATE_TIME) Instant
              from,
      final @RequestParam(name = "to", required = false) @DateTimeFormat(iso = DATE_TIME) Instant
              to) {
    logger.info(
        "Fetch documents metadata bu user:{} and by extension (optionally):{} using date range starting from:{} to:{}.",
        userId,
        extension,
        from,
        to);
    Set<Document> documentsByUserId =
        documentManagement.getDocumentsByUserId(userId, extension, from, to);
    Set<DocumentMetadataResponse> documentsMetadata =
        documentsByUserId.stream()
            .map(DocumentRestMapper::mapDocumentToDocumentMetadataResponseDto)
            .collect(toSet());
    return new ResponseEntity<>(documentsMetadata, HttpStatus.OK);
  }
}
