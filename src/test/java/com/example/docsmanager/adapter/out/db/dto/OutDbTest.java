package com.example.docsmanager.adapter.out.db.dto;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class OutDbTest {

  private static final String OUT_DB_DTO_PACKAGE = "com.example.docsmanager.adapter.out.db.dto";

  @Test
  void outDbDTOsTest() {
    EqualsVerifier.forPackage(OUT_DB_DTO_PACKAGE).verify();
  }
}
