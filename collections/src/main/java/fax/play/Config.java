package fax.play;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.impl.ConfigurationProperties;
import org.infinispan.commons.configuration.StringConfiguration;
import org.infinispan.commons.marshall.ProtoStreamMarshaller;

public class Config {

   private static final String CACHE_DEFINITION =
         "<local-cache name=\"collections\" statistics=\"true\">" +
         "    <encoding media-type=\"application/x-protostream\"/>" +
         "</local-cache>";

   private final RemoteCacheManager remoteCacheManager;
   private final RemoteCache<String, Container> cache;

   public Config() {
      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.addServer().host("127.0.0.1").port(ConfigurationProperties.DEFAULT_HOTROD_PORT)
            .security()
            .authentication()
            .username("user")
            .password("pass")
            .marshaller(ProtoStreamMarshaller.class)
            // Register proto schema && entity marshallers on client side
            .addContextInitializer(Container.ModelSchema.INSTANCE);

      remoteCacheManager = new RemoteCacheManager(builder.build());

      // We don't need to register proto schema on server side (we don't have indexes)

      remoteCacheManager.administration().removeCache("collections");
      remoteCacheManager.administration().createCache("collections", new StringConfiguration(CACHE_DEFINITION));

      cache = remoteCacheManager.getCache("collections");
   }

   public void shutdown() {
      remoteCacheManager.stop();
   }

   public RemoteCache<String, Container> cache() {
      return cache;
   }
}
