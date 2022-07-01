package fax.play;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class SmokeTest {

   private RemoteCacheContainer cacheManager;

   @Test
   public void test() {
      RemoteCache<Object, Shape> cache = Config.setupAll();
      cacheManager = cache.getRemoteCacheContainer();
   }

   @AfterEach
   public void end() {
      cacheManager.stop();
   }
}
