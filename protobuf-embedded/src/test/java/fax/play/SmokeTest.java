package fax.play;

import static org.assertj.core.api.Assertions.assertThat;

import org.infinispan.query.Search;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;
import org.infinispan.query.dsl.QueryResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SmokeTest {

   private Config config;

   @BeforeEach
   public void before() {
      config = new Config();
   }

   @AfterEach
   public void after() {
      config.stop();
   }

   @Test
   public void test() {
      config.cache().put(1, new Game("Civilization 1", "The best video game of all time!"));

      QueryFactory factory = Search.getQueryFactory(config.cache());
      Query<Game> query = factory.create("from fax.play.Game where description : 'game'");
      QueryResult<Game> result = query.execute();

      assertThat(result.hitCount()).hasValue(1L);
      assertThat(result.list()).extracting("name").contains("Civilization 1");
   }
}
