import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.transaction.TransactionManager;

import org.infinispan.Cache;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.query.Search;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import fax.play.Config;
import fax.play.Game;
import fax.play.helper.TransactionHelper;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GameTest {

   // from https://en.wikipedia.org/wiki/Tennis
   private static final String TENNIS_DESC = "Tennis is a racket sport that can be played individually against a single opponent (singles) or between two teams of two players each (doubles). Each player uses a tennis racket that is strung with cord to strike a hollow rubber ball covered with felt over or around a net and into the opponent's court. The object of the game is to manoeuvre the ball in such a way that the opponent is not able to play a valid return. The player who is unable to return the ball validly will not gain a point, while the opposite player will.";
   // from https://en.wikipedia.org/wiki/Football
   private static final String FOOTBALL_DESC = "Football is a family of team sports that involve, to varying degrees, kicking a ball to score a goal. Unqualified, the word football normally means the form of football that is the most popular where the word is used. Sports commonly called football include association football (known as soccer in North America and Oceania); gridiron football (specifically American football or Canadian football); Australian rules football; rugby union and rugby league; and Gaelic football.[1] These various forms of football share to varying extent common origins and are known as football codes.";

   public EmbeddedCacheManager cacheManager;

   @BeforeAll
   public void setUp() {
      cacheManager = Config.start();
   }

   @AfterAll
   public void tearDown() {
      if (cacheManager != null) {
         cacheManager.stop();
      }
   }

   @Test
   public void test() throws Exception {
      Cache<Object, Object> cache = cacheManager.getCache(Config.CACHE_NAME);
      TransactionManager transactionManager = cache.getAdvancedCache().getTransactionManager();

      TransactionHelper.withinTransaction(transactionManager, () -> {
         cache.put("tennis", new Game("Tennis", TENNIS_DESC));
         cache.put("football", new Game("Football", FOOTBALL_DESC));
      });

      QueryFactory queryFactory = Search.getQueryFactory(cache);

      Query<Game> query = queryFactory.create("from fax.play.Game where name = 'Tennis'");
      List<Game> matches = query.execute().list();
      assertThat(matches).extracting("name").containsExactly("Tennis");

      query = queryFactory.create("from fax.play.Game where description : 'involve'");
      matches = query.execute().list();
      assertThat(matches).extracting("name").containsExactly("Football");

      Query<Object[]> projection = queryFactory.create("select name from fax.play.Game");
      List<Object[]> results = projection.execute().list();
      assertThat(results).hasSize(2);

      TransactionHelper.withinTransaction(transactionManager, () -> {
         Query<Object> command = queryFactory.create("delete from fax.play.Game");
         int size = command.executeStatement();
         assertThat(size).isZero(); // no transaction (???)
      });

      projection = queryFactory.create("select name from fax.play.Game");
      results = projection.execute().list();
      assertThat(results).hasSize(2);
   }
}
