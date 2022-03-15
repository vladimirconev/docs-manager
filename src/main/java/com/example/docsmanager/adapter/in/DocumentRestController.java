package com.example.docsmanager.adapter.in;

import com.example.docsmanager.adapter.in.dto.DocumentMetadataResponseDto;
import com.example.docsmanager.adapter.in.dto.ErrorResponseDto;
import com.example.docsmanager.domain.DocumentManagement;
import com.example.docsmanager.domain.entity.Document;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@Api(tags = { "Documents" }, value = "/")
@RequestMapping(
  value = {
    "#{'${enable.api.versioning:true}' ? '/api/v' + '${application.version}'.split('\\.')[0]:'' }",
  }
)
public class DocumentRestController {

  private final DocumentManagement documentManagement;

  @ApiOperation(value = "Retrieve a document by ID", tags = { "Documents" })
  @ApiResponses(
    value = {
      @ApiResponse(code = 200, message = "OK", response = byte[].class),
      @ApiResponse(
        code = 503,
        message = "Service temporally unavailable",
        response = ErrorResponseDto.class
      ),
      @ApiResponse(code = 404, message = "Not Found", response = ErrorResponseDto.class),
    }
  )
  @GetMapping(
    path = "/documents/{documentId}",
    produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
  )
  ResponseEntity<byte[]> getDocumentContent(
    final @PathVariable("documentId") String documentId
  ) {
    log.info("Fetching Document Data content with id:{}.", documentId);
    byte[] content = documentManagement.getDocumentContent(documentId);
    return new ResponseEntity<>(content, HttpStatus.OK);
  }


  @ApiOperation(value = "Upload documents", tags = { "Documents" })
  @ApiResponses(
    value = {
      @ApiResponse(
        code = 201,
        message = "CREATED",
        responseContainer = "List",
        response = DocumentMetadataResponseDto.class
      ),
      @ApiResponse(
        code = 503,
        message = "Service temporally unavailable",
        response = ErrorResponseDto.class
      ),
    }
  )
  @PostMapping(
    path = "/documents",
    produces = MediaType.APPLICATION_JSON_VALUE,
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  ResponseEntity<List<DocumentMetadataResponseDto>> uploadDocuments(
    final @RequestPart("files") MultipartFile[] files,
    final @RequestParam("userId") String userId
  ) {
    log.info("Start of Uploading documents for user: {}.", userId);
    List<Document> documents = DocumentRestMapper.mapMultipartFilesToDocuments(
      Arrays.asList(files),
      userId
    );
    List<Document> uploadedDocuments = documentManagement.uploadDocuments(documents);
    return new ResponseEntity<>(
      DocumentRestMapper.mapDocumentsToDocumentMetadataResponseDtos(uploadedDocuments),
      HttpStatus.CREATED
    );
  }

  @ApiOperation(value = "Delete a document by ID", tags = { "Documents" })
  @ApiResponses(
    value = {
      @ApiResponse(code = 204, message = "NO_CONTENT"),
      @ApiResponse(
        code = 503,
        message = "Service temporally unavailable",
        response = ErrorResponseDto.class
      ),
    }
  )
  @DeleteMapping(path = "/documents")
  ResponseEntity<Void> deleteDocuments(
    final @RequestParam("documentIds") Set<String> documentIds
  ) {
    log.info("Deleting documents with following IDs:{}.", documentIds);
    documentManagement.deleteDocuments(documentIds);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @ApiOperation(
    value = "Retrieve a document by user id and extension",
    tags = { "Documents" }
  )
  @ApiResponses(
    value = {
      @ApiResponse(
        code = 200,
        message = "OK",
        responseContainer = "Set",
        response = DocumentMetadataResponseDto.class
      ),
      @ApiResponse(
        code = 503,
        message = "Service temporally unavailable",
        response = ErrorResponseDto.class
      ),
    }
  )
  @GetMapping(path = "/documents", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Set<DocumentMetadataResponseDto>> getDocumentsByUserId(
    final @RequestParam("userId") String userId,
    final @RequestParam(name = "extension", required = false) String extension
  ) {
    log.info(
      "Fetch documents metadata bu user:{} and by extension (optionally):{}.",
      userId,
      extension
    );
    Set<Document> documentsByUserId = documentManagement.getDocumentsByUserId(
      userId,
      extension
    );
    Set<DocumentMetadataResponseDto> documentMetadataDtos = documentsByUserId
      .stream()
      .map(DocumentRestMapper::mapDocumentToDocumentMetadataResponseDto)
      .collect(Collectors.toSet());
    return new ResponseEntity<>(documentMetadataDtos, HttpStatus.OK);
  }
}
