package fax.play.config;

import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.IndexStorage;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

import fax.play.model.IndexedContainer;

public class CacheManagerFactory {

   public static final String BASIC_CACHE_NAME = "basic-cache";
   public static final String INDEXED_CACHE_NAME = "indexed-cache";

   public static EmbeddedCacheManager create() {
      EmbeddedCacheManager cacheManager = new DefaultCacheManager();

      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder
            // configure indexing only to support the indexed sorting
            // not required by BasicContainer for instance
            .indexing()
               .enable()
               .storage(IndexStorage.LOCAL_HEAP)
               .addIndexedEntity(IndexedContainer.class);

      cacheManager.createCache(BASIC_CACHE_NAME, new ConfigurationBuilder().build());
      cacheManager.createCache(INDEXED_CACHE_NAME, builder.build());
      return cacheManager;
   }
}
