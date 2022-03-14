package com.example.docsmanager.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;

import com.example.docsmanager.TestObjectFactory;
import com.example.docsmanager.domain.entity.Document;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DocumentManagerTest extends TestObjectFactory {

  @InjectMocks
  private DocumentManager documentManager;

  @Mock
  private DocumentManagementRepository docsManagementRepo;

  @Test
  void uploadDocumentsTest() {
    Document document = buildDocumentInstance(
      DOCUMENT_ID,
      LocalDateTime.now(),
      BYTE_CONTENT,
      SAMPLE_USER_ID
    );
    Mockito
      .when(docsManagementRepo.uploadDocuments(Mockito.anyList()))
      .thenReturn(List.of(document));

    List<Document> output = documentManager.uploadDocuments(List.of(document));

    assertNotNull(output);

    if (output.stream().findAny().isPresent()) {
      var actual = output.stream().findAny().get();
      assertEquals(document, actual);
    }

    Mockito.verify(docsManagementRepo, times(1)).uploadDocuments(Mockito.anyList());
    Mockito.verifyNoMoreInteractions(docsManagementRepo);
  }

  @Test
  void deleteDocumentsTest() {
    documentManager.deleteDocuments(Set.of(DOCUMENT_ID));

    Mockito.verify(docsManagementRepo, times(1)).deleteDocuments(Mockito.anySet());
    Mockito.verifyNoMoreInteractions(docsManagementRepo);
  }

  @Test
  void getDocumentContentTest() {
    Mockito
      .when(docsManagementRepo.getDocumentContent(DOCUMENT_ID))
      .thenReturn(BYTE_CONTENT);

    byte[] documentContent = documentManager.getDocumentContent(DOCUMENT_ID);

    assertNotNull(documentContent);
    assertEquals(BYTE_CONTENT, documentContent);

    Mockito.verify(docsManagementRepo, times(1)).getDocumentContent(DOCUMENT_ID);
    Mockito.verifyNoMoreInteractions(docsManagementRepo);
  }
}
