package fax.play.service;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.impl.ConfigurationProperties;
import org.infinispan.commons.marshall.ProtoStreamMarshaller;
import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;

public class CacheFactory {

   public static RemoteCacheManager create() {
      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.addServer().host("127.0.0.1").port(ConfigurationProperties.DEFAULT_HOTROD_PORT)
            .security()
            .authentication()
            .username("user")
            .password("pass")
            .marshaller(ProtoStreamMarshaller.class);

      return new RemoteCacheManager(builder.build());
   }

   public static RemoteCacheManager create(CacheDefinition cacheDefinition, GeneratedSchema ... schemas) {
      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.addServer().host("127.0.0.1").port(ConfigurationProperties.DEFAULT_HOTROD_PORT)
            .security()
            .authentication()
            .username("user")
            .password("pass");

      if (cacheDefinition != null) {
         builder.remoteCache(cacheDefinition.getName())
               .configuration(cacheDefinition.getConfiguration())
               .marshaller(ProtoStreamMarshaller.class);
      }

      for (GeneratedSchema schema : schemas) {
         // Add marshaller in the client
         builder.addContextInitializer(schema);
      }

      RemoteCacheManager remoteCacheManager = new RemoteCacheManager(builder.build());

      for (GeneratedSchema schema : schemas) {
         // Register proto schema on server side
         RemoteCache<String, String> metadataCache = remoteCacheManager.getCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME);
         metadataCache.put(schema.getProtoFileName(), schema.getProtoFile());
      }

      return remoteCacheManager;
   }

}
