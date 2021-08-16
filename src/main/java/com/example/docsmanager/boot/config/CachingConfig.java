package com.example.docsmanager.boot.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Caching configuration using Caffeine. <br/>
 * @see <a href="https://github.com/ben-manes/caffeine">https://github.com/ben-manes/caffeine</a>
 *
 * @author Vladimir.Conev
 *
 */
@Configuration
@EnableCaching
public class CachingConfig {

  @Bean
  public Caffeine<Object, Object> caffeineConfig() {
    return Caffeine
      .newBuilder()
      .expireAfterWrite(10, TimeUnit.MINUTES)
      .initialCapacity(30);
  }

  @Bean
  public CacheManager cacheManager(final Caffeine<Object, Object> caffeine) {
    CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
    caffeineCacheManager.getCache("docs_byte_content");
    caffeineCacheManager.setCaffeine(caffeine);
    return caffeineCacheManager;
  }
}
