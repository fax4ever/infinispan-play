package fax.play;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.impl.ConfigurationProperties;
import org.infinispan.commons.configuration.StringConfiguration;
import org.infinispan.commons.marshall.ProtoStreamMarshaller;

public class Config {

   private static final String CACHE_DEFINITION =
         "<local-cache name=\"veri\" statistics=\"true\">" +
         "    <encoding media-type=\"application/x-protostream\"/>" +
         "</local-cache>";

   private final RemoteCacheManager remoteCacheManager;
   private final RemoteCache<String, Veri> cache;

   public Config() {
      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.addServer().host("127.0.0.1").port(ConfigurationProperties.DEFAULT_HOTROD_PORT)
            .security()
            .authentication()
            .username("user")
            .password("pass")
            .marshaller(ProtoStreamMarshaller.class)
            // Register proto schema && entity marshallers on client side
            .addContextInitializer(Veri.ModelSchema.INSTANCE);

      remoteCacheManager = new RemoteCacheManager(builder.build());

      // Register proto schema on server side
      RemoteCache<String, String> metadataCache = remoteCacheManager.getCache("___protobuf_metadata");
      metadataCache.put(Veri.ModelSchema.INSTANCE.getProtoFileName(), Veri.ModelSchema.INSTANCE.getProtoFile());

      remoteCacheManager.administration().removeCache("veri");
      remoteCacheManager.administration().createCache("veri", new StringConfiguration(CACHE_DEFINITION));

      cache = remoteCacheManager.getCache("veri");
   }

   public void shutdown() {
      remoteCacheManager.stop();
   }

   public RemoteCache<String, Veri> cache() {
      return cache;
   }
}
