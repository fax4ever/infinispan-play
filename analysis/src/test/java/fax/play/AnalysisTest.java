package fax.play;

import static org.assertj.core.api.Assertions.assertThat;

import org.infinispan.client.hotrod.RemoteCache;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import fax.play.config.Config;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AnalysisTest {

   private Config config;
   private RemoteCache<Object, Object> cache;

   @Test
   public void test() {
      assertThat(cache).isNotNull();
   }

   @BeforeAll
   public void beforeAll() throws Exception {
      config = new Config();
      cache = config.getOrCreateCache();
   }

   @AfterAll
   public void afterAll() {
      if (cache != null) {
         cache.clear();
      }
      if (config != null) {
         config.close();
      }
   }
}
