package fax.play;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.Search;
import org.infinispan.client.hotrod.exceptions.HotRodClientException;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;
import org.infinispan.query.dsl.QueryResult;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import fax.play.config.Config;
import fax.play.model.KeywordMessage;
import fax.play.model.TextMessage;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AnalysisTest {

   private String[] MESSAGES = {
         "First message for Ickle query",
         "Second message for Ickle query",
         "A notification",
         "Another message",
         "Another message for Ickle-Query",
         "And another message for .Ickle.Query. with dots",
         "And another message to check query Ickle will be found",
         "And another message to check query MyIckleBla will be found"
   };

   private Config config;
   private RemoteCache<Object, Object> cache;

   @Test
   public void testQueries() {
      assertThat(cache).isNotNull();

      QueryFactory queryFactory = Search.getQueryFactory(cache);
      Query<KeywordMessage> kQuery = queryFactory.create("from KeywordMessage k where k.message = 'Ickle'");
      QueryResult<KeywordMessage> kResult = kQuery.execute();
      assertThat(kResult.hitCount()).hasValue(0); // no match in this case

      kQuery = queryFactory.create("from KeywordMessage k where k.message = '*Ickle*'");
      kResult = kQuery.execute();
      assertThat(kResult.hitCount()).hasValue(0); // no match in this case

      assertThatThrownBy(() -> queryFactory.create("from KeywordMessage k where k.message : '*Ickle*'").execute())
            // no full text query can be performed on Keyword field
            .isInstanceOf(HotRodClientException.class)
            .hasMessageContaining("Full-text queries cannot be applied to property 'message'");

      kQuery = queryFactory.create("from KeywordMessage k where k.message = 'a NOTificatiON'");
      kResult = kQuery.execute();
      assertThat(kResult.hitCount()).hasValue(1); // match with the entire text, with any case since a lowercase normalized

      Query<TextMessage> tQuery = queryFactory.create("from TextMessage t where t.message : 'Ickle'");
      QueryResult<TextMessage> tResult = tQuery.execute();
      assertThat(tResult.hitCount()).hasValue(4); // a text field matches on term presence
   }

   @BeforeAll
   public void beforeAll() throws Exception {
      config = new Config();
      cache = config.getOrCreateCache();
      putData();
   }

   @AfterAll
   public void afterAll() {
      if (config != null) {
         config.close();
      }
   }

   private void putData() {
      int i = 0;
      for (String message : MESSAGES) {
         cache.put("k" + i, new KeywordMessage(message));
         cache.put("t" + i++, new TextMessage(message));
      }
   }
}
