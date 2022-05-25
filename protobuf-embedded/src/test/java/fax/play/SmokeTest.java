package fax.play;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SmokeTest {

   private Config config;

   @BeforeEach
   public void before() {
      config = new Config(System.getProperty("java.io.tmpdir"));
   }

   @AfterEach
   public void after() {
      config.stop();
   }

   @Test
   public void test() {
      config.cache().put(1, new Game("Civilization 1", "The best video game of all time!"));
   }
}
