package fax.play;

import static org.assertj.core.api.Assertions.assertThat;

import org.infinispan.client.hotrod.RemoteCache;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ContainerTest {

   private Config config;
   private RemoteCache<String, Container> cache;

   @BeforeAll
   public void start() {
      config = new Config();
      cache = config.cache();
   }

   @AfterAll
   public void end() {
      if (config != null) {
         config.shutdown();
      }
   }

   @BeforeEach
   public void beforeEach() {
      cache.clear();
   }

   @Test
   public void test() {
      cache.put("1", new Container(new Content("some-content")));

      Container container = cache.get("1");
      assertThat(container).extracting("content").extracting("value").isEqualTo("some-content");
   }
}
