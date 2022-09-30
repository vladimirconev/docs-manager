package com.example.docsmanager.adapter.in.dto;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class InDtoTest {

  private static final String IN_DTO_PACKAGE = "com.example.docsmanager.adapter.in.dto";

  @Test
  void inDTOsTest() {
    EqualsVerifier.forPackage(IN_DTO_PACKAGE).verify();
  }
}
