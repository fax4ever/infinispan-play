package fax.play.service;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.protostream.GeneratedSchema;

public class CacheProvider {

   private RemoteCacheManager cacheManager;

   public RemoteCacheManager init(GeneratedSchema schema, String cacheName) {
      cacheManager = CacheFactory.create();
      cacheManager.administration().removeCache(CacheFactory.CACHE1_NAME);
      cacheManager.administration().removeCache(CacheFactory.CACHE2_NAME);
      cacheManager = CacheFactory.create(schema, cacheName);
      return cacheManager;
   }

   public RemoteCacheManager updateSchemaAndGet(GeneratedSchema schema, String cacheName) {
      stop();
      cacheManager = CacheFactory.create(schema, cacheName);
      return cacheManager;
   }

   public void stop() {
      if (cacheManager != null) {
         cacheManager.stop();
      }
   }
}
