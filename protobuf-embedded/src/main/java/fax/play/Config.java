package fax.play;

import org.infinispan.Cache;
import org.infinispan.commons.dataconversion.MediaType;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.IndexStorage;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.globalstate.ConfigurationStorage;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public class Config {

   private static final String CACHE_NAME = "my-cache";

   private final EmbeddedCacheManager cacheManager;
   private final Cache<Integer, Game> cache;

   public Config(String persistentLocation) {
      GlobalConfigurationBuilder global = new GlobalConfigurationBuilder().nonClusteredDefault();
      global.globalState().enable()
            .persistentLocation(persistentLocation)
            .temporaryLocation(persistentLocation + "/tmp")
            .configurationStorage(ConfigurationStorage.OVERLAY);
      global.serialization().addContextInitializer(GameSchema.INSTANCE);

      cacheManager = new DefaultCacheManager(global.build());
      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.encoding()
            .mediaType(MediaType.APPLICATION_PROTOSTREAM_TYPE)
            .indexing()
            .enable()
            .storage(IndexStorage.LOCAL_HEAP)
            .addIndexedEntity(Game.class);

      cacheManager.administration().removeCache(CACHE_NAME);
      cache = cacheManager.administration().getOrCreateCache(CACHE_NAME, builder.build());
   }

   public Cache<Integer, Game> cache() {
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
