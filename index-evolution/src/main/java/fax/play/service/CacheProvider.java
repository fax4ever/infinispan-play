package fax.play.service;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.commons.marshall.ProtoStreamMarshaller;
import org.infinispan.protostream.GeneratedSchema;

public class CacheProvider {

   public static final String CACHE1_NAME = "keyword1";
   public static final String CACHE2_NAME = "keyword2";

   private final ProtoStreamMarshaller marshaller;

   private RemoteCacheManager cacheManager;

   public CacheProvider() {
      this.marshaller = new ProtoStreamMarshaller();
   }

   public RemoteCacheManager init(CacheDefinition cacheDefinition, GeneratedSchema ... schemas) {
      cacheManager = CacheFactory.create(marshaller);
      cacheManager.administration().removeCache(CACHE1_NAME);
      cacheManager.administration().removeCache(CACHE2_NAME);
      cacheManager = CacheFactory.create(cacheDefinition, marshaller, schemas);
      return cacheManager;
   }

   public RemoteCacheManager getCacheManager() {
      return cacheManager;
   }

   public RemoteCacheManager recreateRemoteCacheManager(CacheDefinition cacheDefinition, GeneratedSchema ... schemas) {
      stop();
      cacheManager = CacheFactory.create(cacheDefinition, marshaller, schemas);
      return cacheManager;
   }

   public RemoteCacheManager recreateRemoteCacheManager(GeneratedSchema ... schemas) {
      stop();
      cacheManager = CacheFactory.create(null, marshaller, schemas);
      return cacheManager;
   }

   public void updateIndexSchema(RemoteCache<String, Model> cache, GeneratedSchema schema) {
      CacheFactory.updateSchemaIndex(cache, marshaller, schema);
   }

   public void stop() {
      if (cacheManager != null) {
         cacheManager.stop();
      }
   }
}
