package fax.play.service;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheContainer;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.impl.ConfigurationProperties;
import org.infinispan.commons.configuration.StringConfiguration;
import org.infinispan.commons.marshall.ProtoStreamMarshaller;
import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.SerializationContextInitializer;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;

public class CacheFactory {

   public static RemoteCacheManager create(ProtoStreamMarshaller marshaller) {
      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.addServer().host("127.0.0.1").port(ConfigurationProperties.DEFAULT_HOTROD_PORT)
            .security()
            .authentication()
            .username("user")
            .password("pass")
            .marshaller(marshaller);

      return new RemoteCacheManager(builder.build());
   }

   public static RemoteCacheManager create(CacheDefinition cacheDefinition, ProtoStreamMarshaller marshaller,
                                           GeneratedSchema ... schemas) {
      RemoteCacheManager remoteCacheManager = create(marshaller);

      for (GeneratedSchema schema : schemas) {
         // Register proto schema && entity marshaller on client side
         schema.registerSchema(marshaller.getSerializationContext());
         schema.registerMarshallers(marshaller.getSerializationContext());
      }

      for (GeneratedSchema schema : schemas) {
         // Register proto schema on server side
         RemoteCache<String, String> metadataCache = remoteCacheManager.getCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME);
         metadataCache.put(schema.getProtoFileName(), schema.getProtoFile());
      }

      if (cacheDefinition != null) {
         remoteCacheManager.administration().removeCache(cacheDefinition.getName());
         remoteCacheManager.administration().createCache(cacheDefinition.getName(),
               new StringConfiguration(cacheDefinition.getConfiguration()));
      }

      return remoteCacheManager;
   }

   public static void updateSchemaIndex(RemoteCache<String, Model> cache, ProtoStreamMarshaller marshaller,
                                        SerializationContextInitializer schema) {

      RemoteCacheContainer remoteCacheContainer = cache.getRemoteCacheContainer();

      // Register proto schema && entity marshaller on client side
      schema.registerSchema(marshaller.getSerializationContext());
      schema.registerMarshallers(marshaller.getSerializationContext());

      // Register proto schema && entity marshaller on server side
      RemoteCache<String, String> metadataCache = remoteCacheContainer.getCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME);
      metadataCache.put(schema.getProtoFileName(), schema.getProtoFile());

      ((RemoteCacheManager) remoteCacheContainer).administration()
            .updateIndexSchema(cache.getName());
   }
}
