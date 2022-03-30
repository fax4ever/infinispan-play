package fax.play.smoke;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.Search;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import fax.play.entity.Developer;
import fax.play.service.Cache;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SmokeTest {

   private RemoteCacheManager cacheManager;

   @BeforeAll
   public void beforeAll() {
      cacheManager = Cache.create();
   }

   @AfterAll
   public void afterAll() {
      if (cacheManager != null) {
         cacheManager.stop();
      }
   }

   @Test
   public void test() {
      RemoteCache<String, Developer> cache = cacheManager.getCache(Cache.CACHE_NAME);
      cache.put("fabio", new Developer("fax4ever", "Java Go"));
      cache.put("alessia", new Developer("always7pan", "Java C++ Cobol Python"));
      cache.put("nenna", new Developer("vale5paga", "C# Lisp Cobol"));
      cache.put("antonia", new Developer("antonia3mini", "Java C Prolog"));

      QueryFactory factory = Search.getQueryFactory(cache);
      Query<Developer> query = factory.create("from fax.play.entity.Developer where languages : 'Java'");
      List<Developer> list = query.execute().list();
      assertThat(list).extracting("nick").containsExactlyInAnyOrder("fax4ever", "always7pan", "antonia3mini");

      query = factory.create("from fax.play.entity.Developer order by nick");
      list = query.execute().list();
      assertThat(list).extracting("nick").containsExactly("always7pan", "antonia3mini", "fax4ever", "vale5paga");
   }
}
