package fax.play;

import org.infinispan.Cache;
import org.infinispan.commons.api.CacheContainerAdmin;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public class Config {

   public static final String CACHE_NAME = "aggregation-play-cache";

   private EmbeddedCacheManager cacheManager;
   private Cache<String, Server> cache;

   public Cache<String, Server> getCache() {
      if (cache != null) {
         return cache;
      }

      GlobalConfigurationBuilder globalConfig = new GlobalConfigurationBuilder().nonClusteredDefault();
      cacheManager = new DefaultCacheManager(globalConfig.build());

      ConfigurationBuilder config = new ConfigurationBuilder();
      cacheManager.administration().removeCache(CACHE_NAME);
      cache = cacheManager.administration()
            .withFlags(CacheContainerAdmin.AdminFlag.VOLATILE)
            .createCache(CACHE_NAME, config.build());
      return cache;
   }

   public void stop() {
      try {
         if (cache != null) {
            cache.stop();
         }
      } finally {
         if (cacheManager != null) {
            cacheManager.stop();
         }
      }
   }

}
