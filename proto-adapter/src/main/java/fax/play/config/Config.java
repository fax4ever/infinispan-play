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
import org.infinispan.protostream.SerializationContext;

import fax.play.model.CachedAnimal;

public class Config implements Closeable {

   public static final String CACHE_NAME = "any-cache";
   private final RemoteCacheManager cacheManager;
   private final ProtoStreamMarshaller protoMarshaller = new ProtoStreamMarshaller();

   public Config() {
      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.addServer().host("127.0.0.1").port(ConfigurationProperties.DEFAULT_HOTROD_PORT)
            .security()
            .authentication()
            .username("user")
            .password("pass")
            .marshaller(protoMarshaller)
            // handle the schema client side
//            .addContextInitializer(new AnimalSchemaImpl())
            ;

      cacheManager = new RemoteCacheManager(builder.build());
   }

   public RemoteCache<String, CachedAnimal> recreateCache() throws IOException {
      cacheManager.administration().removeCache(CACHE_NAME);

      String configuration;
      try (InputStream resourceAsStream = Config.class.getClassLoader().getResourceAsStream("any-cache.yaml")) {
         configuration = new String(resourceAsStream.readAllBytes(), StandardCharsets.UTF_8);
      }

      return cacheManager.administration().createCache(CACHE_NAME, new StringConfiguration(configuration));
   }

   public SerializationContext getSerializationContext() {
      return protoMarshaller.getSerializationContext();
   }

   @Override
   public void close() {
      cacheManager.close();
   }
}
