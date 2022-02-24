package fax.play;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;

import org.infinispan.Cache;
import org.infinispan.objectfilter.ParsingException;
import org.infinispan.query.Search;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;
import org.infinispan.query.dsl.QueryResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SmokeTest {

   private final Config config = new Config();
   private Cache<String, Server> cache;

   @BeforeEach
   public void before() {
      cache = config.getCache();
      cache.clear();

      Game game1 = new Game();
      game1.gameMode = "A";
      game1.playerCount = 3;
      Game game2 = new Game();
      game2.gameMode = "B";
      game2.playerCount = 2;

      Server server1 = new Server();
      server1.id = "s1";
      server1.totalPlayerCount = 5;
      server1.games = Arrays.asList(game1, game2);

      cache.put("server1", server1);
   }

   @AfterEach
   public void after() {
      config.stop();
   }

   @Test
   public void test() {
      QueryFactory factory = Search.getQueryFactory(cache);
      Query<Long> query = factory.create("SELECT SUM(s.games.playerCount) FROM fax.play.Server s WHERE s.id = 's1' GROUP BY s.id");
      QueryResult<Long> queryResult = query.execute();
      assertThat(queryResult).isNotNull();


      assertThatThrownBy(() -> {
         factory.create("SELECT SUM(s.games.playerCount) FROM fax.play.Server s WHERE s.games.gameMode = 'A' GROUP BY s.id").execute();
      })
            .isInstanceOf(ParsingException.class)
            .hasMessageContaining("Unknown alias: games");
   }

}
