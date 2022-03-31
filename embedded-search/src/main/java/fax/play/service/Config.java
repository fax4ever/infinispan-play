package fax.play.service;

import org.infinispan.commons.dataconversion.MediaType;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.IndexStorage;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

import fax.play.entity.Developer;
import fax.play.entity.SchemaImpl;

public class Config {

   public static final String CACHE_NAME = "my-cache";

   public static EmbeddedCacheManager start() {
      GlobalConfigurationBuilder global = new GlobalConfigurationBuilder()
            .nonClusteredDefault();
      global.serialization()
            .addContextInitializer(new SchemaImpl());

      EmbeddedCacheManager cacheManager = new DefaultCacheManager(global.build());

      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder
            .encoding()
               .mediaType(MediaType.APPLICATION_PROTOSTREAM_TYPE)
            .indexing()
               .enable()
               .storage(IndexStorage.LOCAL_HEAP)
               .addIndexedEntity(Developer.class);

      cacheManager.createCache(CACHE_NAME, builder.build());
      return cacheManager;
   }
}
