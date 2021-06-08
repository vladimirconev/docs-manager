package com.example.docsmanager.adapter.out;

import java.util.NoSuchElementException;
import java.util.Set;

import com.example.docsmanager.adapter.out.db.dto.DocumentElasticDto;
import com.example.docsmanager.domain.DocumentManagementRepository;
import com.example.docsmanager.domain.entity.Document;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ElasticDocumentManagerRepository implements DocumentManagementRepository {

	private final DocumentElasticRepository documentElasticRepository;
	
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

}
