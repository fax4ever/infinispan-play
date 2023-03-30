package fax.play.container;

import static org.assertj.core.api.Assertions.assertThat;

import org.infinispan.client.hotrod.DefaultTemplate;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.server.test.core.InfinispanContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InfinispanContainerTest {

   private static final String CACHE_NAME = "my-cache";

   private InfinispanContainer container = new InfinispanContainer();
   private RemoteCacheManager cacheManager;
   private RemoteCache<String, String> cache;

   @BeforeAll
   public void setup() {
      container.start();
      cacheManager = container.getRemoteCacheManager();
      cache = cacheManager.administration().createCache(CACHE_NAME, DefaultTemplate.DIST_SYNC);
   }

   @AfterAll
   public void cleanup() {
      // dispose client resources
      if (cache != null) {
         cache.stop(); // this is optional
      }
      if (cacheManager != null) {
         cacheManager.close(); // or stop that is the same
      }

      // dispose server resources
      if (container != null) {
         container.close(); // or stop that is the same
      }
   }

   @Test
   public void test() {
      cache.put("Hello", "World");

      String value = cache.get("Hello");
      assertThat(value).isEqualTo("World");
   }
}
