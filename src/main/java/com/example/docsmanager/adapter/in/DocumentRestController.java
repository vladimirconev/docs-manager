package com.example.docsmanager.adapter.in;


import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.docsmanager.adapter.in.dto.DocumentMetadataResponseDto;
import com.example.docsmanager.domain.DocumentManagement;
import com.example.docsmanager.domain.entity.Document;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DocumentRestController {
	
	private final DocumentManagement documentManagent;
	
	
	@GetMapping(path = "/documents/{documentId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	ResponseEntity<byte[]> getDocumentContent(final @PathVariable("documentId") String documentId) {
		log.info("Fetching Document Data with id:{}.", documentId);
		byte[] content = documentManagent.getDocumentContent(documentId);
		return new ResponseEntity<>(content, HttpStatus.OK);
	}
	
	@PostMapping(path = "/documents", 
			produces = MediaType.APPLICATION_JSON_VALUE, 
			consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	ResponseEntity<DocumentMetadataResponseDto> uploadDocument(
			final @RequestPart("file") MultipartFile multiPartfile,
			final @RequestParam("userId") String user) {
		log.info("Starting of Uploading a document.");
		Document document = DocumentRestMapper.mapMultipartFileToDocument(multiPartfile, 
				user);
		Document uploadedDocument = documentManagent.uploadDocument(document);
		return new ResponseEntity<>(DocumentRestMapper.mapDocumentToDocumentMetadataResponseDto(uploadedDocument), 
				HttpStatus.OK);
	
	}
	
	@DeleteMapping(path= "/documents",
			produces = MediaType.APPLICATION_JSON_VALUE, 
			consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> deleteDocuments(
			final @RequestParam("documentIds") Set<String> documentIds) {
		log.info("Deleting documents via following IDs:{}.", documentIds);
		documentManagent.deleteDocuments(documentIds);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	

}
