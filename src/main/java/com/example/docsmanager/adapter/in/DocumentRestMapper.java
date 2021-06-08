package com.example.docsmanager.adapter.in;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.mime.MimeTypeException;
import org.springframework.web.multipart.MultipartFile;

import com.example.docsmanager.adapter.in.dto.DocumentMetadataResponseDto;
import com.example.docsmanager.domain.entity.Document;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DocumentRestMapper {

	public static DocumentMetadataResponseDto mapDocumentToDocumentMetadataResponseDto(final Document document) {
		var documentMetadataResponseDto = new DocumentMetadataResponseDto();
		documentMetadataResponseDto.setId(document.getId());
		documentMetadataResponseDto.setExtension(document.getExtension());
		documentMetadataResponseDto.setFileName(document.getFileName());
		documentMetadataResponseDto.setUserId(document.getUserId());
		documentMetadataResponseDto.setCreationDate(document.getCreationDate()
				.format(DateTimeFormatter.ISO_DATE_TIME));
		return documentMetadataResponseDto;
	}

	public static Document mapMultipartFileToDocument(final MultipartFile multipartFile, 
			final String userId) {
		var documentId = UUID.randomUUID().toString();
		var fileName = StringUtils.substringBeforeLast(multipartFile.getOriginalFilename(), ".");
		var fileExtension = multipartFile.getContentType();
		try {
			var apacheTikaConfig = TikaConfig.getDefaultConfig();
			var extension = apacheTikaConfig.getMimeRepository().forName(multipartFile.getContentType()).getExtension();
			fileExtension = StringUtils.substringAfterLast(extension, ".");
		} catch (MimeTypeException ex) {
			log.error("Exception while extracting extension for file:{}.", multipartFile.getOriginalFilename());
		}
		byte[] content;
		try {
			content = IOUtils.toByteArray(multipartFile.getInputStream());
		} catch (IOException ioException) {
			throw new IllegalStateException("Error while extracting byte array content.");
		}
		return new Document(
				documentId, 
				fileName, 
				fileExtension, 
				LocalDateTime.now(), 
				content,
				userId);
	}

}
