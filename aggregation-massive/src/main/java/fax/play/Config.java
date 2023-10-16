package fax.play;

import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.IndexStorage;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public class Config {

   public static EmbeddedCacheManager cacheManager() {
      GlobalConfigurationBuilder global = new GlobalConfigurationBuilder().nonClusteredDefault();
      global.defaultCacheName("blablabla");

      ConfigurationBuilder config = new ConfigurationBuilder();
      config.indexing()
            .enable()
            .storage(IndexStorage.LOCAL_HEAP)
            .addIndexedEntities(Sale.class);

      return new DefaultCacheManager(global.build(), config.build());
   }
}
