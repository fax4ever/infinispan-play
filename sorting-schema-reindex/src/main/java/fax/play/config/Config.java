package fax.play.config;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.exceptions.HotRodClientException;
import org.infinispan.client.hotrod.impl.ConfigurationProperties;
import org.infinispan.commons.configuration.StringConfiguration;
import org.infinispan.commons.marshall.ProtoStreamMarshaller;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;

import fax.play.model.Developer.DeveloperSchema;

public class Config implements Closeable {

   public static final String CACHE_NAME = "developer";
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
            .addContextInitializer(DeveloperSchema.INSTANCE);

      cacheManager = new RemoteCacheManager(builder.build());

      cacheManager.getCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME)
            // handle the schema server side
            .put(DeveloperSchema.INSTANCE.getProtoFileName(), DeveloperSchema.INSTANCE.getProtoFile());
   }

   public RemoteCache<Object, Object> recreateCache() throws IOException {
      cacheManager.administration().removeCache(CACHE_NAME);

      String configuration;
      try (InputStream resourceAsStream = Config.class.getClassLoader().getResourceAsStream("developer.yaml")) {
         configuration = new String(resourceAsStream.readAllBytes(), StandardCharsets.UTF_8);
      }

      return cacheManager.administration().createCache(CACHE_NAME, new StringConfiguration(configuration));
   }

   public void updateProtoSchemaServerSide() throws Exception {
      try (InputStream resourceAsStream = Config.class.getClassLoader().getResourceAsStream("developer-sortable.proto")) {
         String newProtoFile = new String(resourceAsStream.readAllBytes(), StandardCharsets.UTF_8);

         cacheManager.getCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME)
               // handle the schema server side
               .put(DeveloperSchema.INSTANCE.getProtoFileName(), newProtoFile);
      }

      // cacheManager.administration().updateIndexSchema(CACHE_NAME);
      cacheManager.administration().reindexCache(CACHE_NAME);
   }

   @Override
   public void close() {
      cacheManager.close();
   }
}
