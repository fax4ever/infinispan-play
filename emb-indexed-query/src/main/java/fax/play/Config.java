package fax.play;

import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.IndexStorage;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public class Config {

   public static final String CACHE_NAME = "my-cache";

   public static EmbeddedCacheManager start() {
      EmbeddedCacheManager cacheManager = new DefaultCacheManager();

      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.indexing()
            .enable()
            .storage(IndexStorage.LOCAL_HEAP)
            .addIndexedEntity(Game.class);

      cacheManager.createCache(CACHE_NAME, builder.build());
      return cacheManager;
   }
}
