package fax.play.container;

import static org.assertj.core.api.Assertions.assertThat;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.test.testcontainers.InfinispanContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class InfinispanContainerTest {

   @Container
   private final static InfinispanContainer INFINISPAN_SERVER = new InfinispanContainer().withCache("mycache");

   @AfterAll
   public static void cleanup() {
      INFINISPAN_SERVER.stop();
   }

   @Test
   public void test() {
      RemoteCache<String, String> cache = INFINISPAN_SERVER.getCacheManager().getCache("mycache");
      assertThat(cache).isNotNull();
   }
}
