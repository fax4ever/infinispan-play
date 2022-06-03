package fax.play.service;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.protostream.GeneratedSchema;

public class CacheProvider {

   public static final String CACHE1_NAME = "keyword1";
   public static final String CACHE2_NAME = "keyword2";

   private RemoteCacheManager cacheManager;

   public RemoteCacheManager init(CacheDefinition cacheDefinition, GeneratedSchema ... schemas) {
      cacheManager = CacheFactory.create();
      cacheManager.administration().removeCache(CACHE1_NAME);
      cacheManager.administration().removeCache(CACHE2_NAME);
      cacheManager = CacheFactory.create(cacheDefinition, schemas);
      return cacheManager;
   }

   public RemoteCacheManager getCacheManager() {
      return cacheManager;
   }

   public RemoteCacheManager updateSchemaAndGet(CacheDefinition cacheDefinition, GeneratedSchema ... schemas) {
      stop();
      cacheManager = CacheFactory.create(cacheDefinition, schemas);
      return cacheManager;
   }

   public RemoteCacheManager updateSchemaAndGet(GeneratedSchema ... schemas) {
      stop();
      cacheManager = CacheFactory.create(null, schemas);
      return cacheManager;
   }

   public void updateIndexSchema(String ... cacheNames) {
      for (String cacheName : cacheNames) {
         cacheManager.administration().updateIndexSchema(cacheName);
      }
   }

   public void reindexCaches(String ... cacheNames) {
      for (String cacheName : cacheNames) {
         cacheManager.administration().reindexCache(cacheName);
      }
   }

   public void stop() {
      if (cacheManager != null) {
         cacheManager.stop();
      }
   }
}
