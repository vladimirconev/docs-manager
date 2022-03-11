package com.example.docsmanager.adapter.out.db.dto;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

public class OutDbDtoTest {

  private static final String OUT_DB_DTO_PACKAGE =
    "com.example.docsmanager.adapter.out.db.dto";

  @Test
  void outDbDTOsTest() {
    EqualsVerifier.forPackage(OUT_DB_DTO_PACKAGE).verify();
  }
}
