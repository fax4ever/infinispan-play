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

public class Config implements Closeable {

   public static final String CACHE_NAME = "company";
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
            .addContextInitializer(new CompanySCI());

      cacheManager = new RemoteCacheManager(builder.build());
   }

   public RemoteCache<Object, Object> recreateCache() throws IOException {
      cacheManager.administration().removeCache(CACHE_NAME);

      String configuration;
      try (InputStream resourceAsStream = Config.class.getClassLoader().getResourceAsStream("company.yaml")) {
         configuration = new String(resourceAsStream.readAllBytes(), StandardCharsets.UTF_8);
      }

      return cacheManager.administration().createCache(CACHE_NAME, new StringConfiguration(configuration));
   }

   @Override
   public void close() {
      cacheManager.close();
   }
}
