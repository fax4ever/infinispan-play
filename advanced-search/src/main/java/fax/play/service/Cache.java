package fax.play.service;

import java.net.URI;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.impl.ConfigurationProperties;
import org.infinispan.commons.marshall.ProtoStreamMarshaller;
import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;

import fax.play.entity.SchemaImpl;

public class Cache {

   public static final String CACHE_NAME = "developer";

   public static RemoteCacheManager create() {
      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.addServer().host("127.0.0.1").port(ConfigurationProperties.DEFAULT_HOTROD_PORT)
            .security()
            .authentication()
            .username("admin")
            .password("password");

      builder.remoteCache(CACHE_NAME)
            .configurationURI(URI.create("local-cache.xml"))
            .marshaller(ProtoStreamMarshaller.class);

      SchemaImpl schema = new SchemaImpl();
      builder.addContextInitializer(schema);

      RemoteCacheManager remoteCacheManager = new RemoteCacheManager(builder.build());
      RemoteCache<String, String> metadataCache = remoteCacheManager
            .getCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME);
      metadataCache.put(schema.getProtoFileName(), schema.getProtoFile());

      return remoteCacheManager;
   }

}
