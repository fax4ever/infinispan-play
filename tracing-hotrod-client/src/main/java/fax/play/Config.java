package fax.play;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.impl.ConfigurationProperties;
import org.infinispan.commons.configuration.StringConfiguration;

public class Config {

   private static final String CACHE_NAME = "blablabla";
   private static final String CACHE_DEFINITION = "<local-cache name=\"blablabla\"></local-cache>";

   private final RemoteCacheManager remoteCacheManager;

   public Config() {
      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.addServer().host("127.0.0.1").port(ConfigurationProperties.DEFAULT_HOTROD_PORT)
            .security()
            .authentication()
            .username("user")
            .password("pass");

      remoteCacheManager = new RemoteCacheManager(builder.build());

      remoteCacheManager.administration().removeCache(CACHE_NAME);
      remoteCacheManager.administration().createCache(CACHE_NAME, new StringConfiguration(CACHE_DEFINITION));
   }

   public void shutdown() {
      remoteCacheManager.stop();
   }

   public RemoteCache<Integer, String> getCache() {
      return remoteCacheManager.getCache(CACHE_NAME);
   }
}
