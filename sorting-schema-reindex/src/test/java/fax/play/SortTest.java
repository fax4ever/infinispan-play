package fax.play;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.Search;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;
import org.infinispan.query.dsl.QueryResult;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import fax.play.config.Config;
import fax.play.model.Developer;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SortTest {

   private Config config;
   private RemoteCache<Object, Object> cache;

   @BeforeAll
   public void beforeAll() throws Exception {
      config = new Config();
      cache = config.recreateCache();
   }

   @AfterAll
   public void afterAll() {
      if (config != null) {
         config.close();
      }
   }

   @Test
   public void testQueries() throws Exception {
      assertThat(cache).isNotNull();

      cache.put(1, new Developer("fax4ever", 300));

      QueryFactory queryFactory = Search.getQueryFactory(cache);
      Query<Developer> kQuery = queryFactory.create("from Developer d order by d.contributions");
      QueryResult<Developer> kResult = kQuery.execute();
      assertThat(kResult.hitCount()).hasValue(1);

      config.updateProtoSchemaServerSide();

      kQuery = queryFactory.create("from Developer d order by d.contributions");
      kResult = kQuery.execute();
      assertThat(kResult.hitCount()).hasValue(1);
   }
}
