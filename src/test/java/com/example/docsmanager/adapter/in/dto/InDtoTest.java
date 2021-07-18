package com.example.docsmanager.adapter.in.dto;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

public class InDtoTest {
	
	private static final String IN_DTO_PACKAGE = "com.example.docsmanager.adapter.in.dto";
	
	
	@Test
	void inDTOsTest() {
		 Reflections reflections = new Reflections(IN_DTO_PACKAGE, new SubTypesScanner(false));
		 Set<Class<?>> classes = reflections.getSubTypesOf(Object.class)
		      .stream()
		      .collect(Collectors.toSet());
		 
		 classes.forEach(classType -> {
			 assertThat(classType, allOf(hasValidBeanConstructor(), hasValidBeanEquals(), hasValidGettersAndSetters(),
		                hasValidBeanHashCode(), hasValidBeanToString()));
		 });
	}

}
