package fax.play;

import static org.assertj.core.api.Assertions.assertThat;

import org.infinispan.client.hotrod.RemoteCache;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SmokeTest {

   private Config config;

   @BeforeAll
   public void start() {
      config = new Config();
   }

   @AfterAll
   public void end() {
      if (config != null) {
         config.shutdown();
      }
   }

   @Test
   public void legalValues() {
      RemoteCache<String, Veri> cache = config.cache();

      cache.put("useCode", new Veri("bla", "theCode", null));
      cache.put("useVirtual", new Veri("blabla", null, 777));

      assertThat(cache.get("useCode")).isEqualTo(new Veri("bla", "theCode", null));
      assertThat(cache.get("useVirtual")).isEqualTo(new Veri("blabla", null, 777));

      cache.clear();
   }

   @Test
   public void differentValues() {
      RemoteCache<String, Veri> cache = config.cache();

      cache.put("useBoth", new Veri("bla", "theCode", 739));
      cache.put("useVirtual", new Veri("blabla", null, 777));

      Veri useBoth = cache.get("useBoth");

      assertThat(useBoth).isEqualTo(new Veri("bla", "theCode", 739));
      assertThat(cache.get("useVirtual")).isEqualTo(new Veri("blabla", null, 777));

      cache.clear();
   }
}
