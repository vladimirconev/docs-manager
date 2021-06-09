package com.example.docsmanager;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.example.docsmanager.domain.entity.Document;

public class TestObjectFactory {
	
	protected byte[] BYTE_CONTENT = new byte[] {1,2,3,8};
	protected String PNG_EXTENSION = "png";
	protected String FILE_NAME = "test_file";
	protected String SAMPLE_USER_ID = "foo";
	protected String DOCUMENT_ID = UUID.randomUUID().toString();
	
	
	protected Document buildDocumentInstance(final String id, 
			final LocalDateTime creationDate,
			final byte[] content) {
		return new Document(id, FILE_NAME, PNG_EXTENSION, LocalDateTime.now(), 
				content, SAMPLE_USER_ID);
	}
	
	
	protected MultipartFile buildMockMultipartFile() {
		return new MockMultipartFile(FILE_NAME, FILE_NAME.concat(".").concat(PNG_EXTENSION),
				"image/png", BYTE_CONTENT);
	}

}
