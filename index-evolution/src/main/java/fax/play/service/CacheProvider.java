package fax.play.service;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.protostream.GeneratedSchema;

public class CacheProvider {

   private RemoteCacheManager cacheManager;

   public RemoteCache<String, Model> init(GeneratedSchema schema) {
      cacheManager = CacheFactory.create();
      cacheManager.administration().removeCache(CacheFactory.CACHE_NAME);
      cacheManager = CacheFactory.create(schema);
      return cacheManager.getCache(CacheFactory.CACHE_NAME);
   }

   public RemoteCache<String, Model> updateSchemaAndGet(GeneratedSchema schema) {
      stop();
      cacheManager = CacheFactory.create(schema);
      return cacheManager.getCache(CacheFactory.CACHE_NAME);
   }

   public void stop() {
      if (cacheManager != null) {
         cacheManager.stop();
      }
   }
}
