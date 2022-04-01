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
import fax.play.service.Config;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SmokeTest {

   private RemoteCacheManager cacheManager;

   @BeforeAll
   public void beforeAll() {
      cacheManager = Config.start();
   }

   @AfterAll
   public void afterAll() {
      if (cacheManager != null) {
         cacheManager.stop();
      }
   }

   @Test
   public void test() {
      RemoteCache<String, Developer> cache = cacheManager.getCache(Config.CACHE_NAME);
      cache.clear();

      cache.put("fabio", new Developer("fax4ever", "Java Go", 2));
      cache.put("alessia", new Developer("always7pan", "Java C++ Cobol Python", 7));
      cache.put("nenna", new Developer("vale5paga", "C# Lisp Cobol", 3));
      cache.put("antonia", new Developer("antonia3mini", "Java C*a Prolog", 3));

      QueryFactory factory = Search.getQueryFactory(cache);
      Query<Developer> query = factory.create("from fax.play.entity.Developer where languages : 'Java'");
      List<Developer> list = query.execute().list();
      assertThat(list).extracting("nick").containsExactlyInAnyOrder("fax4ever", "always7pan", "antonia3mini");

      query = factory.create("from fax.play.entity.Developer order by nick");
      list = query.execute().list();
      assertThat(list).extracting("nick").containsExactly("always7pan", "antonia3mini", "fax4ever", "vale5paga");

      for (int i = 0; i < 20; i++) {
         String id = (i < 10) ? "d0" + i : "d" + i;
         cache.put(id, new Developer(id, "Java Go", i % 20));
      }

      query = factory.create("from fax.play.entity.Developer d WHERE d.projects IN (:id0, :id1, :id2, :id3, :id4, :id5, :id6, :id7, :id8, :id9, :id10, :id11, :id12, :id13, :id14, :id15, :id16, :id17, :id18, :id19) and d.languages : 'Java' order by d.nick");
      for (int i = 0; i < 20; i++) {
         query.setParameter("id" + i, i);
      }
      query.startOffset(10);
      query.maxResults(-1);
      list = query.execute().list();

      // ISPN-13702 With max result -1 we get no result
      // assertThat(list).extracting("nick").containsExactly("d08", "d09", "d10", "d11", "d12", "d13", "d14", "d15", "d16", "d17", "d18", "d19", "fax4ever");
      assertThat(list).isEmpty();
   }
}
