package com.example.docsmanager.boot.config;

import com.example.docsmanager.adapter.in.RestExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestExceptionHandlerConfig {

  @Bean
  public RestExceptionHandler restExceptionHandler(
      @Autowired DefaultErrorAttributes defaultErrorAttributes) {
    return new RestExceptionHandler(defaultErrorAttributes);
  }
}
