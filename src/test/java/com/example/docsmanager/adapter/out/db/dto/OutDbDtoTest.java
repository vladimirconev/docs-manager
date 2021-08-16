package com.example.docsmanager.adapter.out.db.dto;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

public class OutDbDtoTest {

  private static final String OUT_DB_DTO_PACKAGE =
    "com.example.docsmanager.adapter.out.db.dto";

  @Test
  void outDbDTOsTest() {
    Reflections reflections = new Reflections(
      OUT_DB_DTO_PACKAGE,
      new SubTypesScanner(false)
    );
    Set<Class<?>> classes = reflections
      .getSubTypesOf(Object.class)
      .stream()
      .collect(Collectors.toSet());

    classes.forEach(
      classType -> {
        assertThat(
          classType,
          allOf(
            hasValidBeanConstructor(),
            hasValidBeanEquals(),
            hasValidGettersAndSetters(),
            hasValidBeanHashCode(),
            hasValidBeanToString()
          )
        );
      }
    );
  }
}
