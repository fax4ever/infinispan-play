package fax.play.config;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.impl.ConfigurationProperties;
import org.infinispan.commons.configuration.StringConfiguration;
import org.infinispan.commons.marshall.ProtoStreamMarshaller;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;

import fax.play.model.MessageSchema;

public class Config implements Closeable {

   private final RemoteCacheManager cacheManager;

   public Config() {
      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.addServer().host("127.0.0.1").port(ConfigurationProperties.DEFAULT_HOTROD_PORT)
            .security()
            .authentication()
            .username("user")
            .password("pass")
            .marshaller(ProtoStreamMarshaller.class)
            // handle the schema client side
            .addContextInitializer(MessageSchema.INSTANCE);

      cacheManager = new RemoteCacheManager(builder.build());

      cacheManager.getCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME)
            // handle the schema server side
            .put(MessageSchema.INSTANCE.getProtoFileName(), MessageSchema.INSTANCE.getProtoFile());
   }

   public RemoteCache<Object, Object> getOrCreateCache() throws IOException {
      String configuration;
      try (InputStream resourceAsStream = Config.class.getClassLoader().getResourceAsStream("message.yaml")) {
         configuration = new String(resourceAsStream.readAllBytes(), StandardCharsets.UTF_8);
      }

      return cacheManager.administration().getOrCreateCache("messages", new StringConfiguration(configuration));
   }

   @Override
   public void close() {
      cacheManager.close();
   }
}
