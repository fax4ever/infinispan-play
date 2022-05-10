package fax.play;

import org.infinispan.Cache;
import org.infinispan.commons.dataconversion.MediaType;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.IndexStorage;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public class Config {

   public static final String CACHE_NAME = "my-cache";

   public static Cache<Object, Model> createCache() {
      GlobalConfigurationBuilder global = new GlobalConfigurationBuilder()
            .nonClusteredDefault();
      global.serialization().addContextInitializer(SchemaA.INSTANCE);

      EmbeddedCacheManager cacheManager = new DefaultCacheManager(global.build());

      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.encoding().mediaType(MediaType.APPLICATION_PROTOSTREAM_TYPE)
            .indexing().enable()
               .storage(IndexStorage.LOCAL_HEAP)
               .addIndexedEntity(ModelA.class);

      return cacheManager.createCache(CACHE_NAME, builder.build());
   }
}
